package com.teste.creditas.credit_simulator.adapters.inbound

import com.teste.creditas.credit_simulator.aplication.service.LoanSimulationService
import com.teste.creditas.credit_simulator.aplication.service.port.SimulationQueuePort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.time.LocalDate

class LoanControllerTest {

    private lateinit var loanSimulationService: LoanSimulationService
    private lateinit var simulationQueuePort: SimulationQueuePort
    private lateinit var controller: LoanController

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        loanSimulationService = mock(LoanSimulationService::class.java)
        simulationQueuePort = mock(SimulationQueuePort::class.java)
        controller = LoanController(loanSimulationService, simulationQueuePort)
    }

    @Test
    fun `simulateLoan should return simulation result`() {
        val request = LoanSimulationRequest(
            loanAmount = 15000.0,
            months = 36,
            birthDate = LocalDate.of(1995, 8, 20),
            name = "Rai Nascimento",
            email = "rai.nascimento@email.com"
        )
        val expectedResponse = LoanSimulationResponse(15000.0, 1500.0, 16500.0)

        `when`(loanSimulationService.simulate(request)).thenReturn(expectedResponse)

        val response = controller.simulateLoan(request)

        assertEquals(expectedResponse, response)
        verify(loanSimulationService).simulate(request)
    }

    @Test
    fun `simulateLoanBulkSync should return list of responses`() {
        val requests = listOf(
            LoanSimulationRequest(
                loanAmount = 15000.0,
                months = 36,
                birthDate = LocalDate.of(1995, 8, 20),
                name = "Rai Nascimento",
                email = "rai.nascimento@email.com"
            ),
            LoanSimulationRequest(
                loanAmount = 20000.0,
                months = 24,
                birthDate = LocalDate.of(1990, 5, 5),
                name = "Ana Silva",
                email = "ana.silva@email.com"
            )
        )
        val responses = listOf(
            LoanSimulationResponse(15000.0, 1500.0, 16500.0),
            LoanSimulationResponse(20000.0, 2000.0, 22000.0)
        )

        for (i in requests.indices) {
            `when`(loanSimulationService.simulateBulk(requests[i])).thenReturn(responses[i])
        }

        val result = controller.simulateLoanBulkSync(requests)

        assertEquals(responses, result)
        for (request in requests) {
            verify(loanSimulationService).simulateBulk(request)
        }
    }

    @Test
    fun `simulateBulk should return list of responses in parallel`() = runBlocking {
        val requests = listOf(
            LoanSimulationRequest(
                loanAmount = 15000.0,
                months = 36,
                birthDate = LocalDate.of(1995, 8, 20),
                name = "Rai Nascimento",
                email = "rai.nascimento@email.com"
            ),
            LoanSimulationRequest(
                loanAmount = 20000.0,
                months = 24,
                birthDate = LocalDate.of(1990, 5, 5),
                name = "Ana Silva",
                email = "ana.silva@email.com"
            )
        )
        val responses = listOf(
            LoanSimulationResponse(15000.0, 1500.0, 16500.0),
            LoanSimulationResponse(20000.0, 2000.0, 22000.0)
        )

        for (i in requests.indices) {
            `when`(loanSimulationService.simulateBulk(requests[i])).thenReturn(responses[i])
        }

        val result = controller.simulateBulk(requests)

        assertEquals(responses, result)
        for (request in requests) {
            verify(loanSimulationService).simulateBulk(request)
        }
    }

    @Test
    fun `simulateAsync should enqueue simulations`() {
        val requests = listOf(
            LoanSimulationRequest(
                loanAmount = 15000.0,
                months = 36,
                birthDate = LocalDate.of(1995, 8, 20),
                name = "Rai Nascimento",
                email = "rai.nascimento@email.com"
            ),
            LoanSimulationRequest(
                loanAmount = 20000.0,
                months = 24,
                birthDate = LocalDate.of(1990, 5, 5),
                name = "Ana Silva",
                email = "ana.silva@email.com"
            )
        )

        controller.simulateAsync(requests)

        verify(simulationQueuePort).enqueueSimulations(requests)
    }
}