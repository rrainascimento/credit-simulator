package com.teste.creditas.credit_simulator.domain.service

import com.teste.creditas.credit_simulator.aplication.service.port.SimulationRepositoryPort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Period

@Component
class LoanCalculator(private val simulationRepositoryPort: SimulationRepositoryPort) {

    fun calculate(request: LoanSimulationRequest): LoanSimulationResponse {
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
            // Fórmula PMT = PV * r / (1 - (1+r)^-n)
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
}
