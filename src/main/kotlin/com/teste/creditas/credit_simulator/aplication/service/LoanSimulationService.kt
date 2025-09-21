package com.teste.creditas.credit_simulator.aplication.service

import com.teste.creditas.credit_simulator.aplication.service.port.SimulationRepositoryPort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import com.teste.creditas.credit_simulator.domain.service.LoanCalculator
import org.springframework.stereotype.Service

@Service
class LoanSimulationService(
    private val loanCalculator: LoanCalculator,
    private val simulationRepository: SimulationRepositoryPort,
) {
    fun simulate(request: LoanSimulationRequest): LoanSimulationResponse {
        val response = loanCalculator.calculate(request)
        simulationRepository.saveSimulation(request, response)
        return response
    }

    fun simulateBulk(request: LoanSimulationRequest): LoanSimulationResponse {
        val response = loanCalculator.calculate(request)
        return response
    }
}