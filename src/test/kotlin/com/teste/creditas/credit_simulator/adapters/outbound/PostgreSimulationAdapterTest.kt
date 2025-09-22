package com.teste.creditas.credit_simulator.adapters.outbound

import com.teste.creditas.credit_simulator.adapters.outbound.entity.LoanSimulationEntity
import com.teste.creditas.credit_simulator.adapters.outbound.repository.LoanSimulationJpaRepository
import com.teste.creditas.credit_simulator.aplication.service.port.SimulationRepositoryPort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import java.time.LocalDate
import kotlin.jvm.java
import kotlin.test.assertEquals

class PostgreSimulationAdapterTest {

    private lateinit var repository: LoanSimulationJpaRepository
    private lateinit var adapter: SimulationRepositoryPort

    @BeforeEach
    fun setup() {
        repository = mock(LoanSimulationJpaRepository::class.java)
        adapter = PostgreSimulationAdapter(repository)
    }

    @Test
    fun `saveSimulation should map request and response to entity and save`() {
        val request = LoanSimulationRequest(
            loanAmount = 10000.0,
            months = 12,
            birthDate = LocalDate.of(2000, 1, 1),
            name = "Teste User",
            email = "teste@email.com"
        )
        val response = LoanSimulationResponse(
            totalPayment = 11000.0,
            monthlyPayment = 916.67,
            totalInterest = 1000.0
        )

        val captor = ArgumentCaptor.forClass(LoanSimulationEntity::class.java)

        adapter.saveSimulation(request, response)

        verify(repository).save(captor.capture())
        val savedEntity = captor.value

        assertEquals(request.loanAmount, savedEntity.loanAmount)
        assertEquals(request.birthDate, savedEntity.birthDate)
        assertEquals(request.months, savedEntity.months)
        assertEquals(response.totalPayment, savedEntity.totalPayment)
        assertEquals(response.monthlyPayment, savedEntity.monthlyPayment)
        assertEquals(response.totalInterest, savedEntity.totalInterest)
    }
}