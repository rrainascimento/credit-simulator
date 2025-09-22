package com.teste.creditas.credit_simulator.adapters.outbound.pdf

import com.lowagie.text.Document
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import java.io.ByteArrayOutputStream

object PdfGenerator {
    fun generateSimulationPdf(request: LoanSimulationRequest, response: LoanSimulationResponse): ByteArray {
        val document = Document()
        val outputStream = ByteArrayOutputStream()
        PdfWriter.getInstance(document, outputStream)
        document.open()

        document.add(Paragraph("Simulação de Crédito"))
        document.add(Paragraph("============================================="))
        document.add(Paragraph("Nome: ${request.name}"))
        document.add(Paragraph("Valor solicitado: R$ ${"%.2f".format(request.loanAmount)}"))
        document.add(Paragraph("Valor total à pagar: R$ ${"%.2f".format(response.totalPayment)}"))
        document.add(Paragraph("Prazo: ${request.months} meses"))
        document.add(Paragraph("Parcela mensal: R$ ${"%.2f".format(response.monthlyPayment)}"))
        document.add(Paragraph("Total de juros: R$ ${"%.2f".format(response.totalInterest)}"))

        document.close()
        return outputStream.toByteArray()
    }
}