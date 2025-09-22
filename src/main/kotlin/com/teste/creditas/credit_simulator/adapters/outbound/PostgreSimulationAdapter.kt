package com.teste.creditas.credit_simulator.adapters.outbound

import com.teste.creditas.credit_simulator.adapters.outbound.entity.LoanSimulationEntity
import com.teste.creditas.credit_simulator.adapters.outbound.repository.LoanSimulationJpaRepository
import com.teste.creditas.credit_simulator.aplication.service.port.SimulationRepositoryPort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import org.springframework.stereotype.Component

@Component
class PostgreSimulationAdapter(
    private val repository: LoanSimulationJpaRepository,
) : SimulationRepositoryPort {
    override fun saveSimulation(
        request: LoanSimulationRequest,
        response: LoanSimulationResponse,
    ) {
        val entity =
            LoanSimulationEntity(
                loanAmount = request.loanAmount,
                birthDate = request.birthDate,
                months = request.months,
                totalPayment = response.totalPayment,
                monthlyPayment = response.monthlyPayment,
                totalInterest = response.totalInterest,
            )
        repository.save(entity)
    }
}
