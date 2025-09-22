package com.teste.creditas.credit_simulator.aplication.service

import com.teste.creditas.credit_simulator.aplication.service.port.EmailSenderPort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.argumentCaptor
import java.time.LocalDate

class NotificationServiceTest {
    private lateinit var emailSenderPort: EmailSenderPort
    private lateinit var notificationService: NotificationService

    @BeforeEach
    fun setup() {
        emailSenderPort = mock()
        notificationService = NotificationService(emailSenderPort)
    }

    @Test
    fun `sendSimulationResult should send email with correct content`() {
        val request =
            LoanSimulationRequest(
                loanAmount = 15000.0,
                birthDate = LocalDate.of(1995, 8, 20),
                months = 36,
                name = "Rai Nascimento",
                email = "rai.nascimento@email.com",
            )
        val response =
            LoanSimulationResponse(
                totalPayment = 16500.0,
                monthlyPayment = 1500.0,
                totalInterest = 1500.0,
            )

        // Chama o método
        notificationService.sendSimulationResult(request, response)

        // Captura os argumentos do sendEmail
        val toCaptor = argumentCaptor<String>()
        val subjectCaptor = argumentCaptor<String>()
        val bodyCaptor = argumentCaptor<String>()
        val attachmentCaptor = argumentCaptor<ByteArray>()
        val fileNameCaptor = argumentCaptor<String>()

        verify(emailSenderPort).sendEmail(
            toCaptor.capture(),
            subjectCaptor.capture(),
            bodyCaptor.capture(),
            attachmentCaptor.capture(),
            fileNameCaptor.capture(),
        )

        // Valida argumentos capturados
        assertEquals(request.email, toCaptor.firstValue)
        assertEquals("Simulação de Crédito", subjectCaptor.firstValue)
        assertNotNull(bodyCaptor.firstValue) // Corpo do e-mail gerado
        assertNotNull(attachmentCaptor.firstValue) // PDF gerado
        assertEquals("simulacao_credito.pdf", fileNameCaptor.firstValue)
    }
}
