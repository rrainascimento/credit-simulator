package com.teste.creditas.credit_simulator.aplication.service.port

import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest

interface SimulationQueuePort {
    fun enqueueSimulations(requests: List<LoanSimulationRequest>)
}
