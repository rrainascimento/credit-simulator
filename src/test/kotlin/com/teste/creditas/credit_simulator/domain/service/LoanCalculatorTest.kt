package com.teste.creditas.credit_simulator.domain.service

import com.teste.creditas.credit_simulator.aplication.service.port.SimulationRepositoryPort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Period
import kotlin.test.assertEquals

class LoanCalculatorTest {

    private lateinit var repository: SimulationRepositoryPort
    private lateinit var calculator: LoanCalculator

    @BeforeEach
    fun setup() {
        repository = mock(SimulationRepositoryPort::class.java)
        calculator = LoanCalculator(repository)
    }

    // helper that mirrors the production logic using BigDecimal + HALF_EVEN
    private fun expectedFor(request: LoanSimulationRequest): LoanSimulationResponse {
        val age = Period.between(request.birthDate, LocalDate.now()).years

        val annualRate = when {
            age <= 25 -> BigDecimal("0.05")
            age in 26..40 -> BigDecimal("0.03")
            age in 41..60 -> BigDecimal("0.02")
            else -> BigDecimal("0.04")
        }

        val monthlyRate = annualRate.divide(BigDecimal(12), 10, RoundingMode.HALF_EVEN)
        val n = BigDecimal(request.months)
        val pv = BigDecimal(request.loanAmount)

        val monthlyPayment = if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            pv.divide(n, 2, RoundingMode.HALF_EVEN)
        } else {
            val onePlusR = BigDecimal.ONE + monthlyRate
            val denominator = BigDecimal.ONE - onePlusR.pow(-request.months, MathContext.DECIMAL128)
            pv.multiply(monthlyRate)
                .divide(denominator, 10, RoundingMode.HALF_EVEN)
                .setScale(2, RoundingMode.HALF_EVEN)
        }

        val totalPayment = monthlyPayment.multiply(n).setScale(2, RoundingMode.HALF_EVEN)
        val totalInterest = totalPayment.subtract(pv).setScale(2, RoundingMode.HALF_EVEN)

        return LoanSimulationResponse(
            totalPayment = totalPayment,
            monthlyPayment = monthlyPayment,
            totalInterest = totalInterest
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

        assertEquals(expected, actual)
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

        assertEquals(expected, actual)
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

        assertEquals(expected, actual)
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

        assertEquals(expected, actual)
    }
}