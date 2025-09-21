package com.teste.creditas.credit_simulator.adapters.outbound

import com.teste.creditas.credit_simulator.adapters.outbound.entity.LoanSimulationEntity
import com.teste.creditas.credit_simulator.adapters.outbound.repository.InterestRateJpaRepositor
import com.teste.creditas.credit_simulator.adapters.outbound.repository.LoanSimulationJpaRepository
import com.teste.creditas.credit_simulator.aplication.service.port.SimulationRepositoryPort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import org.springframework.stereotype.Component

@Component
class PostgreSimulationAdapter(
    private val repository: LoanSimulationJpaRepository,
    private val rateRepository: InterestRateJpaRepositor
) : SimulationRepositoryPort {

    override fun saveSimulation(request: LoanSimulationRequest, response: LoanSimulationResponse) {
        val entity = LoanSimulationEntity(
            loanAmount = request.loanAmount,
            birthDate = request.birthDate,
            months = request.months,
            totalPayment = response.totalPayment,
            monthlyPayment = response.monthlyPayment,
            totalInterest = response.totalInterest
        )
        repository.save(entity)
    }

    override fun getLoanRateByAge(age: Int): Double {
        val rate = rateRepository.findByMinAgeLessThanEqualAndMaxAgeGreaterThanEqual(age, age)
            ?: throw IllegalArgumentException("No rate found for age $age")

        return rate.annualRate
    }
}
