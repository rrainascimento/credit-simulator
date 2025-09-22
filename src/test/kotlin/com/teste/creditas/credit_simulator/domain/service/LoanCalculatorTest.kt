package com.teste.creditas.credit_simulator.domain.service

import com.teste.creditas.credit_simulator.aplication.service.port.SimulationRepositoryPort
import org.junit.jupiter.api.Assertions.*
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.time.LocalDate
import java.time.Period
import kotlin.math.pow
import kotlin.math.round

class LoanCalculatorTest {

    private lateinit var repository: SimulationRepositoryPort
    private lateinit var calculator: LoanCalculator

    @BeforeEach
    fun setup() {
        repository = mock(SimulationRepositoryPort::class.java)
        calculator = LoanCalculator(repository)
    }

    // helper that mirrors the production logic exactly (taxa por idade + fórmula PMT + round2)
    private fun expectedFor(request: LoanSimulationRequest): LoanSimulationResponse {
        val age = Period.between(request.birthDate, LocalDate.now()).years

        val annualRate = when {
            age <= 25 -> 0.05
            age in 26..40 -> 0.03
            age in 41..60 -> 0.02
            else -> 0.04
        }

        val monthlyRate = annualRate / 12.0
        val n = request.months
        val pv = request.loanAmount

        val monthlyPayment = if (monthlyRate == 0.0) {
            pv / n
        } else {
            // mesma forma algébrica usada na sua classe:
            // PMT = PV * ( r / (1 - (1+r)^-n ) )
            pv * (monthlyRate / (1 - (1 + monthlyRate).pow(-n)))
        }

        val totalPayment = monthlyPayment * n
        val totalInterest = totalPayment - pv

        fun round2(value: Double): Double = round(value * 100) / 100

        return LoanSimulationResponse(
            totalPayment = round2(totalPayment),
            monthlyPayment = round2(monthlyPayment),
            totalInterest = round2(totalInterest)
        )
    }

    @Test
    fun `age less than or equal 25 uses 5 percent annual rate`() {
        val request = LoanSimulationRequest(
            loanAmount = 10_000.0,
            birthDate = LocalDate.now().minusYears(23),
            months = 12,
            name = "Jovem",
            email = "jovem@example.com"
        )

        val actual = calculator.calculate(request)
        val expected = expectedFor(request)

        assertEquals(expected.totalPayment, actual.totalPayment)
        assertEquals(expected.monthlyPayment, actual.monthlyPayment)
        assertEquals(expected.totalInterest, actual.totalInterest)
    }

    @Test
    fun `age between 26 and 40 uses 3 percent annual rate`() {
        val request = LoanSimulationRequest(
            loanAmount = 20_000.0,
            birthDate = LocalDate.now().minusYears(30),
            months = 24,
            name = "Adulto",
            email = "adulto@example.com"
        )

        val actual = calculator.calculate(request)
        val expected = expectedFor(request)

        assertEquals(expected.totalPayment, actual.totalPayment)
        assertEquals(expected.monthlyPayment, actual.monthlyPayment)
        assertEquals(expected.totalInterest, actual.totalInterest)
    }

    @Test
    fun `age between 41 and 60 uses 2 percent annual rate`() {
        val request = LoanSimulationRequest(
            loanAmount = 30_000.0,
            birthDate = LocalDate.now().minusYears(50),
            months = 36,
            name = "Maduro",
            email = "maduro@example.com"
        )

        val actual = calculator.calculate(request)
        val expected = expectedFor(request)

        assertEquals(expected.totalPayment, actual.totalPayment)
        assertEquals(expected.monthlyPayment, actual.monthlyPayment)
        assertEquals(expected.totalInterest, actual.totalInterest)
    }

    @Test
    fun `age greater than 60 uses 4 percent annual rate`() {
        val request = LoanSimulationRequest(
            loanAmount = 15_000.0,
            birthDate = LocalDate.now().minusYears(65),
            months = 48,
            name = "Idoso",
            email = "idoso@example.com"
        )

        val actual = calculator.calculate(request)
        val expected = expectedFor(request)

        assertEquals(expected.totalPayment, actual.totalPayment)
        assertEquals(expected.monthlyPayment, actual.monthlyPayment)
        assertEquals(expected.totalInterest, actual.totalInterest)
    }
}