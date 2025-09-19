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


# 📈 Credit Simulator API

Este projeto em Kotlin + Spring Boot 3.5.5 permite simular empréstimos, calculando parcelas, juros e total a pagar com base na idade do cliente e nas regras de negócio definidas.

---

## ✅ Requisitos

* Java 17+ instalado (opcional se for usar Docker)
* Maven 3+ instalado (opcional se for usar Docker)
* Docker e Docker Compose (opcional)

---

## 📦 Build e execução da aplicação

### 1. Build com Maven

```bash
mvn clean package
```

O JAR gerado estará em:

```
target/creditsimulator-0.0.1-SNAPSHOT.jar
```

E pode ser executado com:

```bash
java -jar target/creditsimulator-0.0.1-SNAPSHOT.jar
```

---

### 2. Dockerização

Para rodar a aplicação em qualquer máquina com Docker, siga os passos:

#### a) Estrutura de pastas sugerida

```
project-root/
├─ src/
├─ Dockerfile
├─ pom.xml
└─ docker/
   └─ docker-compose.yml
```

#### b) Conteúdo do Dockerfile (na raiz do projeto)

```dockerfile
# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### c) docker-compose.yml (na pasta `docker/`)

```yaml
version: "3.9"

services:
  app:
    build:
      context: ..
      dockerfile: Dockerfile
    image: creditsimulator:latest
    container_name: creditsimulator
    ports:
      - "8080:8080"
    restart: unless-stopped
```

#### d) Rodando a aplicação com Docker Compose

Dentro da raiz do projeto ou usando o caminho relativo:

```bash
docker-compose -f docker/docker-compose.yml up --build
```

A aplicação estará disponível em: [http://localhost:8080](http://localhost:8080)

Para parar:

```bash
docker-compose -f docker/docker-compose.yml down
```

---

## 📚 Arquitetura

* **Controller** → recebe requisições HTTP
* **Service** → contém a lógica de simulação de crédito
* **DTOs** → modelos para requests e responses
* **Utils** → funções auxiliares, como cálculo de parcelas (PMT)
* **Strategy** → para regras de negócio diferenciadas (ex.: cálculo de juros, validações)

### Uso de Mensageria (cenário real)

* O endpoint `simulate/async` poderia enviar cada simulação para um **broker** como Kafka ou SQS.
* Serviços consumidores processariam as simulações em background.
* O usuário poderia consultar o status ou receber notificações quando o cálculo estiver pronto.

---

## 📊 Regras de Simulação de Crédito

* Faixa etária do cliente define a taxa de juros anual:

  * Até 25 anos: 5%
  * 26 a 40 anos: 3%
  * 41 a 60 anos: 2%
  * Acima de 60 anos: 4%
* A fórmula de cálculo da parcela mensal (PMT):

```
PMT = PV * r / (1 - (1 + r)^-n)
```

* PV = Valor do empréstimo
* r = taxa de juros mensal (taxa anual / 12)
* n = número de meses (prazo)
* Total a pagar = PMT \* n
* Total de juros = Total a pagar - PV

---

Desenvolvido com ❤️ usando Kotlin + Spring Boot
