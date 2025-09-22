package com.teste.creditas.credit_simulator.adapters.outbound.email

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Base64

object SendGridRequestBuilder {
    fun buildSendGridRequest(
        to: String,
        subject: String,
        body: String,
        isHtml: Boolean = false,
        attachment: ByteArray? = null,
        fileName: String? = null,
    ): RequestBody {
        val personalization =
            JSONObject()
                .put("to", JSONArray().put(JSONObject().put("email", to)))

        val from =
            JSONObject()
                .put("email", "rrainascimento@gmail.com")
                .put("name", "Simulador de Credito")

        val contentType = if (isHtml) "text/html" else "text/plain"

        val content =
            JSONArray()
                .put(JSONObject().put("type", contentType).put("value", body))

        val root =
            JSONObject()
                .put("personalizations", JSONArray().put(personalization))
                .put("from", from)
                .put("subject", subject)
                .put("content", content)

        if (attachment != null && fileName != null) {
            val pdfBase64 = Base64.getEncoder().encodeToString(attachment)

            val attachmentJson =
                JSONObject()
                    .put("content", pdfBase64)
                    .put("type", "application/pdf")
                    .put("filename", fileName)
                    .put("disposition", "attachment")

            root.put("attachments", JSONArray().put(attachmentJson))
        }

        val jsonString = root.toString()
        return jsonString.toRequestBody("application/json".toMediaType())
    }
}
