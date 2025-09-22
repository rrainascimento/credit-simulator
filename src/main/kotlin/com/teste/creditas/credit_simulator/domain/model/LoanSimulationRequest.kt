package com.teste.creditas.credit_simulator.domain.model

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDate

@Schema(description = "Requisição para simulação de empréstimo")
data class LoanSimulationRequest(
    @Schema(
        description = "Valor do empréstimo solicitado",
        example = "15000.00",
    )
    val loanAmount: Double,
    @Schema(
        description = "Data de nascimento do cliente no formato ISO-8601 (YYYY-MM-DD)",
        example = "1995-08-20",
    )
    val birthDate: LocalDate,
    @Schema(
        description = "Prazo de pagamento em meses",
        example = "36",
    )
    val months: Int,
    @Schema(
        description = "Nome completo do cliente",
        example = "Rai Nascimento",
    )
    val name: String,
    @Schema(
        description = "Email do cliente",
        example = "rai.nascimento@email.com",
    )
    val email: String,
)

@Schema(description = "Resposta com os resultados da simulação de empréstimo")
data class LoanSimulationResponse(
    @Schema(
        description = "Valor total a ser pago ao final do empréstimo",
        example = "18000.00",
    )
    val totalPayment: BigDecimal,
    @Schema(
        description = "Valor da parcela mensal",
        example = "500.00",
    )
    val monthlyPayment: BigDecimal,
    @Schema(
        description = "Total de juros pagos ao longo do empréstimo",
        example = "3000.00",
    )
    val totalInterest: BigDecimal,
)
