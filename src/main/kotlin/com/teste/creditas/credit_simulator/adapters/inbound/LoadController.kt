package com.teste.creditas.credit_simulator.adapters.inbound

import com.teste.creditas.credit_simulator.aplication.service.LoanSimulationService
import com.teste.creditas.credit_simulator.aplication.service.port.SimulationQueuePort
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationRequest
import com.teste.creditas.credit_simulator.domain.model.LoanSimulationResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loan Simulation", description = "APIs para simulação de empréstimos")
class LoanController(
    private val loanSimulationService: LoanSimulationService,
    private val simulationQueuePort: SimulationQueuePort,
) {
    @PostMapping("/simulate")
    @Operation(
        summary = "Simula um único empréstimo",
        description = "Recebe uma requisição com os dados do cliente e retorna os valores calculados.",
    )
    @ApiResponse(responseCode = "200", description = "Simulação realizada com sucesso")
    fun simulateLoan(
        @RequestBody request: LoanSimulationRequest,
    ): LoanSimulationResponse {
        return loanSimulationService.simulate(request)
    }

    @PostMapping("/simulate/bulk/sync")
    @Operation(
        summary = "Simula múltiplos empréstimos sem paralelismo",
        description = "Recebe uma lista de requisições e retorna os resultados de todas as simulações.",
    )
    @ApiResponse(responseCode = "200", description = "Simulação realizada com sucesso")
    fun simulateLoanBulkSync(
        @RequestBody requests: List<LoanSimulationRequest>,
    ): List<LoanSimulationResponse> {
        return requests.map { request ->
            loanSimulationService.simulateBulk(request)
        }
    }

    @PostMapping("/simulate/bulk")
    @Operation(
        summary = "Simula múltiplos empréstimos em paralelo",
        description = "Recebe uma lista de requisições e retorna os resultados de todas as simulações.",
    )
    @ApiResponse(responseCode = "200", description = "Simulações realizadas com sucesso")
    suspend fun simulateBulk(
        @RequestBody requests: List<LoanSimulationRequest>,
    ): List<LoanSimulationResponse> =
        coroutineScope {
            requests.map { request ->
                async { loanSimulationService.simulateBulk(request) }
            }.awaitAll()
        }

    @PostMapping("/simulate/async")
    @Operation(
        summary = "Enfileira múltiplas simulações de empréstimos",
        description = "Recebe uma lista de requisições e as envia para processamento assíncrono via mensageria.",
    )
    @ApiResponse(responseCode = "202", description = "Simulações enfileiradas com sucesso")
    fun simulateAsync(
        @RequestBody requests: List<LoanSimulationRequest>,
    ) {
        simulationQueuePort.enqueueSimulations(requests)
    }
}
