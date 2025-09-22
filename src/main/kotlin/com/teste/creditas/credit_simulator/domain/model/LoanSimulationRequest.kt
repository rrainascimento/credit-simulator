package com.teste.creditas.credit_simulator.domain.model

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate


@Schema(description = "Requisição para simulação de empréstimo")
data class LoanSimulationRequest(

    @Schema(
        description = "Valor do empréstimo solicitado",
        example = "15000.00"
    )
    val loanAmount: Double,

    @Schema(
        description = "Data de nascimento do cliente (formato ISO-8601)",
        example = "1995-08-20"
    )
    val birthDate: LocalDate,

    @Schema(
        description = "Prazo de pagamento em meses",
        example = "36"
    )
    val months: Int,

    val name: String,
    val email: String
)

@Schema(description = "Resposta com os resultados da simulação de empréstimo")
data class LoanSimulationResponse(

    @Schema(
        description = "Valor total a ser pago ao final do empréstimo",
        example = "18000.00"
    )
    val totalPayment: Double,

    @Schema(
        description = "Valor da parcela mensal",
        example = "500.00"
    )
    val monthlyPayment: Double,

    @Schema(
        description = "Total de juros pagos ao longo do empréstimo",
        example = "3000.00"
    )
    val totalInterest: Double,

    val name: String,
    val email: String
)
