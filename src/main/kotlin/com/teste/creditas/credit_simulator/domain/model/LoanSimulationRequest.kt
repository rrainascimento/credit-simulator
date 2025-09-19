package com.teste.creditas.credit_simulator.domain.model

import java.time.LocalDate

data class LoanSimulationRequest(
    val loanAmount: Double,
    val birthDate: LocalDate,
    val months: Int
)

data class LoanSimulationResponse(
    val totalPayment: Double,
    val monthlyPayment: Double,
    val totalInterest: Double
)
