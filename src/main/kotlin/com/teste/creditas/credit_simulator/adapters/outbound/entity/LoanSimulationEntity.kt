package com.teste.creditas.credit_simulator.adapters.outbound.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "loan_simulations")
data class LoanSimulationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val loanAmount: Double,
    val birthDate: LocalDate,
    val months: Int,
    val totalPayment: Double,
    val monthlyPayment: Double,
    val totalInterest: Double,
)
