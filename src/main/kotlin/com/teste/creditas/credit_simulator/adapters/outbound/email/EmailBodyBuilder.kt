package com.teste.creditas.credit_simulator.adapters.outbound.email

import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse

object EmailBodyBuilder {
    fun buildSimulationSummary(
        request: LoanSimulationRequest,
        response: LoanSimulationResponse
    ): String = """
        Olá, ${response.name}, tudo bem?
        
        Aqui está o resultado da sua simulação de crédito:
        
        Valor solicitado: R$ ${"%.2f".format(request.loanAmount)}
        Valor total à pagar: R$ ${"%.2f".format(response.totalPayment)}
        Prazo: ${request.months} meses
        Parcela mensal: R$ ${"%.2f".format(response.monthlyPayment)}
        Total de juros: R$ ${"%.2f".format(response.totalInterest)}
    """.trimIndent()
}