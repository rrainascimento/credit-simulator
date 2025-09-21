package com.teste.creditas.credit_simulator.aplication.service.port

import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse

interface SimulationRepositoryPort {
    fun saveSimulation(request: LoanSimulationRequest, response: LoanSimulationResponse)
}