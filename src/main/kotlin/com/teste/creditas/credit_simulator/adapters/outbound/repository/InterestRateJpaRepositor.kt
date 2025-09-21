package com.teste.creditas.credit_simulator.adapters.outbound.repository

import com.teste.creditas.credit_simulator.adapters.outbound.entity.InterestRateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InterestRateJpaRepositor : JpaRepository<InterestRateEntity, Long> {
    fun findByMinAgeLessThanEqualAndMaxAgeGreaterThanEqual(min: Int, max: Int): InterestRateEntity?
}
