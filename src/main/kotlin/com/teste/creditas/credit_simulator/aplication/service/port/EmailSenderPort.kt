package com.teste.creditas.credit_simulator.aplication.service.port

interface EmailSenderPort {
    fun sendEmail(to: String, subject: String, body: String, attachment: ByteArray? = null, fileName: String? = null)
}