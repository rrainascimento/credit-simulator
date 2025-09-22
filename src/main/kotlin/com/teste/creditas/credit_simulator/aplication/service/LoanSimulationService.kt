package com.teste.creditas.credit_simulator.aplication.service

import com.teste.creditas.credit_simulator.adapters.outbound.pdf.PdfGenerator.generateSimulationPdf
import com.teste.creditas.credit_simulator.aplication.service.port.EmailSenderPort
import com.teste.creditas.credit_simulator.aplication.service.port.SimulationRepositoryPort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import com.teste.creditas.credit_simulator.domain.service.LoanCalculator
import org.springframework.stereotype.Service

@Service
class LoanSimulationService(
    private val loanCalculator: LoanCalculator,
    private val simulationRepository: SimulationRepositoryPort,
    private val notificationService: NotificationService
) {
    fun simulate(request: LoanSimulationRequest): LoanSimulationResponse {
        val response = loanCalculator.calculate(request)
        simulationRepository.saveSimulation(request, response)

        notificationService.sendSimulationResult(request, response)

        return response
    }

    fun simulateBulk(request: LoanSimulationRequest): LoanSimulationResponse {
        val response = loanCalculator.calculate(request)
        return response
    }
}