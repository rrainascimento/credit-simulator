package com.teste.creditas.credit_simulator.adapters.outbound.email

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.springframework.mock.env.MockEnvironment
import kotlin.test.assertFailsWith

class SendGridEmailAdapterTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var emailAdapter: SendGridEmailAdapter

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val env = MockEnvironment()
        env.setProperty("sendgrid.api.key", "fake-api-key")

        val client = OkHttpClient()

        // Substituímos a URL padrão pelo mock
        emailAdapter =
            object : SendGridEmailAdapter(env) {
                override fun sendEmail(
                    to: String,
                    subject: String,
                    body: String,
                    attachment: ByteArray?,
                    fileName: String?,
                ) {
                    val requestBody = SendGridRequestBuilder.buildSendGridRequest(to, subject, body, false, attachment, fileName)

                    val request =
                        okhttp3.Request.Builder()
                            .url(mockWebServer.url("/v3/mail/send")) // <-- mock server endpoint
                            .addHeader("Authorization", "Bearer fake-api-key")
                            .post(requestBody)
                            .build()

                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw RuntimeException("Erro ao enviar email: ${response.code} - ${response.body?.string()}")
                        }
                    }
                }
            }
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `deve enviar email com sucesso`() {
        // Mockando resposta 202 Accepted do SendGrid
        mockWebServer.enqueue(MockResponse().setResponseCode(202))

        emailAdapter.sendEmail(
            to = "teste@dominio.com",
            subject = "Assunto de Teste",
            body = "Corpo do email",
        )

        val recordedRequest = mockWebServer.takeRequest()
        Assertions.assertEquals("/v3/mail/send", recordedRequest.path)
        Assertions.assertEquals("Bearer fake-api-key", recordedRequest.getHeader("Authorization"))
        Assertions.assertTrue(recordedRequest.body.readUtf8().contains("teste@dominio.com"))
    }

    @Test
    fun `deve falhar quando SendGrid retornar erro`() {
        // Mockando erro 400
        mockWebServer.enqueue(MockResponse().setResponseCode(400).setBody("Invalid request"))

        assertFailsWith<RuntimeException> {
            emailAdapter.sendEmail(
                to = "teste@dominio.com",
                subject = "Erro",
                body = "Teste erro",
            )
        }
    }
}
