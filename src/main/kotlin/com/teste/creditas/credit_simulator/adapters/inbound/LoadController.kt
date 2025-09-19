package com.teste.creditas.credit_simulator.adapters.inbound

import com.teste.creditas.credit_simulator.aplication.service.LoanSimulationService
import com.teste.creditas.credit_simulator.aplication.service.port.SimulationQueuePort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/loans")
class LoanController(
    private val loanSimulationService: LoanSimulationService,
    private val simulationQueuePort: SimulationQueuePort
) {

    @PostMapping("/simulate")
    fun simulateLoan(@RequestBody request: LoanSimulationRequest): LoanSimulationResponse {
        return loanSimulationService.simulate(request)
    }

    @PostMapping("/simulate/bulk")
    suspend fun simulateBulk(@RequestBody requests: List<LoanSimulationRequest>): List<LoanSimulationResponse> =
        coroutineScope {
            requests.map { request ->
                async { loanSimulationService.simulate(request) }
            }.awaitAll()
        }

    @PostMapping("/simulate/async")
    fun simulateAsync(@RequestBody requests: List<LoanSimulationRequest>) {
        simulationQueuePort.enqueueSimulations(requests)
    }
}