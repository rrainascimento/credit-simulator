package com.teste.creditas.credit_simulator.adapters.outbound.email

import com.teste.creditas.credit_simulator.aplication.service.port.EmailSenderPort
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class SendGridEmailAdapter(
    private val env: Environment,
    private val client: OkHttpClient = OkHttpClient(),
) : EmailSenderPort {
    private val apiKey: String =
        env.getProperty("sendgrid.api.key")
            ?: throw IllegalStateException("SENDGRID_API_KEY não configurada")

    override fun sendEmail(
        to: String,
        subject: String,
        body: String,
        attachment: ByteArray?,
        fileName: String?,
    ) {
        val requestBody = SendGridRequestBuilder.buildSendGridRequest(to, subject, body, false, attachment, fileName)

        val request =
            Request.Builder()
                .url("https://api.sendgrid.com/v3/mail/send")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(requestBody)
                .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw RuntimeException("Erro ao enviar email: ${response.code} - ${response.body?.string()}")
            }
        }
    }
}
