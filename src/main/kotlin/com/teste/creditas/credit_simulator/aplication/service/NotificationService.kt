package com.teste.creditas.credit_simulator.aplication.service

import com.teste.creditas.credit_simulator.adapters.outbound.email.EmailBodyBuilder
import com.teste.creditas.credit_simulator.adapters.outbound.pdf.PdfGenerator
import com.teste.creditas.credit_simulator.aplication.service.port.EmailSenderPort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val emailSenderPort: EmailSenderPort
) {
    fun sendSimulationResult(request: LoanSimulationRequest, response: LoanSimulationResponse) {
        val resumo = EmailBodyBuilder.buildSimulationSummary(request, response)

        val pdf = PdfGenerator.generateSimulationPdf(
            request,
            response
        )

        emailSenderPort.sendEmail(
            to = request.email,
            subject = "Simulação de Crédito",
            body = resumo,
            attachment = pdf,
            fileName = "simulacao_credito.pdf"
        )
    }
}