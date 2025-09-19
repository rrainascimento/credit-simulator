# ■ Credit Simulator - Kotlin + Spring Boot
Este projeto é um **simulador de crédito** desenvolvido em **Kotlin** com **Spring Boot 3.5.5** e
**Java 21**.
---
## ■ Setup do Projeto
### Pré-requisitos
- **Java 21+**
- **Maven 3.9+**
- **Docker** (opcional, para testes)
### Passos para rodar
1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/credit-simulator.git
cd credit-simulator
```
2. Compile e rode a aplicação:
```bash
./mvnw spring-boot:run
```
3. A aplicação estará disponível em:
```
http://localhost:8080
```
---
## ■ Arquitetura
Foi adotada a **Arquitetura Hexagonal (Ports and Adapters)**:
- **Domain (core business):** regras de simulação de crédito
- **Application (services):** orquestração das regras
- **Adapters (infra):** exposição via REST, mensageria, persistência futura
Benefícios:
- Alta **cohesão** no domínio
- Fácil **testabilidade**
- Redução de **acoplamento** com frameworks externos
---
## ■ Endpoints
### Simulação Única
```http
POST /api/loans/simulate
Content-Type: application/json
{
"valorEmprestimo": 10000,
"dataNascimento": "1990-01-01",
"prazoMeses": 24
}
```
### Simulação em Massa (bulk)
```http
POST /api/loans/simulate/bulk
Content-Type: application/json
[
{ "valorEmprestimo": 10000, "dataNascimento": "1995-01-01", "prazoMeses": 24 },
{ "valorEmprestimo": 20000, "dataNascimento": "1980-06-15", "prazoMeses": 36 }
]
```
---
## ■ Alta Volumetria
- O endpoint `/simulate/bulk` aceita milhares de simulações em uma única requisição.
- Utilizamos **Kotlin Coroutines** para paralelismo assíncrono.
- Em cenários reais, usaríamos **mensageria (Kafka, RabbitMQ, SQS)**:
- O endpoint apenas enfileiraria as simulações
- Um consumidor processaria em background
- Resultados poderiam ser consultados via outro endpoint
---
## ■ Teste de Carga (k6)
Exemplo de teste (`bulk_test.js`):
```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';
export const options = {
vus: 10,
iterations: 1000, // 10.000 requisições
};
export default function () {
const url = 'http://localhost:8080/api/loans/simulate/bulk';
const payload = JSON.stringify([
{ valorEmprestimo: 10000, dataNascimento: '1995-01-01', prazoMeses: 24 },
{ valorEmprestimo: 20000, dataNascimento: '1980-06-15', prazoMeses: 36 }
]);
const params = { headers: { 'Content-Type': 'application/json' } };
const res = http.post(url, payload, params);
check(res, { 'status is 200': (r) => r.status === 200 });
sleep(1);
}
```
Rodando o teste:
```bash
k6 run bulk_test.js
```
---
## ❤■ Desenvolvido com
- Kotlin
- Spring Boot 3.5.5
- Java 21
- Maven