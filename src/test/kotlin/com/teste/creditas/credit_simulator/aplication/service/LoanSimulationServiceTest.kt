package com.teste.creditas.credit_simulator.aplication.service

import com.teste.creditas.credit_simulator.aplication.service.port.SimulationRepositoryPort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import com.teste.creditas.credit_simulator.domain.service.LoanCalculator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

class LoanSimulationServiceTest {

    private lateinit var loanCalculator: LoanCalculator
    private lateinit var simulationRepository: SimulationRepositoryPort
    private lateinit var notificationService: NotificationService
    private lateinit var service: LoanSimulationService

    @BeforeEach
    fun setup() {
        loanCalculator = mock()
        simulationRepository = mock()
        notificationService = mock()

        service = LoanSimulationService(loanCalculator, simulationRepository, notificationService)
    }

    @Test
    fun `simulate should calculate, save and send notification`() {
        val request = LoanSimulationRequest(
            loanAmount = 15000.0,
            months = 36,
            birthDate = LocalDate.of(1995, 8, 20),
            name = "Rai Nascimento",
            email = "rai.nascimento@email.com"
        )
        val expectedResponse = LoanSimulationResponse(15000.0, 1500.0, 16500.0)

        whenever(loanCalculator.calculate(request)).thenReturn(expectedResponse)

        val response = service.simulate(request)

        // Verifica se retornou a resposta esperada
        assertEquals(expectedResponse, response)

        // Verifica se salvou no repositório
        verify(simulationRepository).saveSimulation(eq(request), eq(expectedResponse))

        // Verifica se enviou a notificação
        verify(notificationService).sendSimulationResult(eq(request), eq(expectedResponse))

        // Verifica se calculou
        verify(loanCalculator).calculate(eq(request))
    }

    @Test
    fun `simulateBulk should calculate and return response without saving or notifying`() {
        val request = LoanSimulationRequest(
            loanAmount = 20000.0,
            months = 24,
            birthDate = LocalDate.of(1990, 5, 5),
            name = "Ana Silva",
            email = "ana.silva@email.com"
        )
        val expectedResponse = LoanSimulationResponse(20000.0, 2000.0, 22000.0)

        whenever(loanCalculator.calculate(request)).thenReturn(expectedResponse)

        val response = service.simulateBulk(request)

        assertEquals(expectedResponse, response)

        // Nenhum save ou notificação deve ocorrer
        verify(simulationRepository, never()).saveSimulation(any(), any())
        verify(notificationService, never()).sendSimulationResult(any(), any())

        // Calculou apenas
        verify(loanCalculator).calculate(eq(request))
    }
}
