package com.teste.creditas.credit_simulator.domain.service

import com.teste.creditas.credit_simulator.aplication.service.port.SimulationRepositoryPort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.Period
import kotlin.math.pow

@Component
class LoanCalculator(private val simulationRepositoryPort: SimulationRepositoryPort) {

    fun calculate(request: LoanSimulationRequest): LoanSimulationResponse {
        val age = Period.between(request.birthDate, LocalDate.now()).years

        val annualRate =
            when {
                age <= 25 -> 0.05
                age in 26..40 -> 0.03
                age in 41..60 -> 0.02
                else -> 0.04
            }

        val monthlyRate = annualRate / 12
        val n = request.months
        val pv = request.loanAmount

        // Fórmula PMT = PV * r / (1 - (1+r)^-n)
        val monthlyPayment =
            if (monthlyRate == 0.0) {
                pv / n
            } else {
                pv * (monthlyRate / (1 - (1 + monthlyRate).pow(-n)))
            }

        val totalPayment = monthlyPayment * n
        val totalInterest = totalPayment - pv

        return LoanSimulationResponse(
            totalPayment = round2(totalPayment),
            monthlyPayment = round2(monthlyPayment),
            totalInterest = round2(totalInterest),
        )
    }

    private fun round2(value: Double): Double = kotlin.math.round(value * 100) / 100
}
