package com.teste.creditas.credit_simulator.adapters.outbound

import com.teste.creditas.credit_simulator.aplication.service.port.SimulationQueuePort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InMemorySimulationQueueAdapter : SimulationQueuePort {
    private val logger = LoggerFactory.getLogger(InMemorySimulationQueueAdapter::class.java)

    override fun enqueueSimulations(requests: List<LoanSimulationRequest>) {
        // Aqui abstraímos o envio para Kafka, RabbitMQ ou SQS
        logger.info("Enfileirando ${requests.size} simulações... (mock)")
    }
}
