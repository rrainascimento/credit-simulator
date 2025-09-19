package com.teste.creditas.credit_simulator.aplication.service

import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import com.teste.creditas.credit_simulator.domain.service.LoanCalculator
import org.springframework.stereotype.Service

@Service
class LoanSimulationService(
    private val loanCalculator: LoanCalculator = LoanCalculator()
) {
    fun simulate(request: LoanSimulationRequest): LoanSimulationResponse {
        return loanCalculator.calculate(request)
    }
}