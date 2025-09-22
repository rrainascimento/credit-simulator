# 📈 Credit Simulator API

Este projeto, desenvolvido em **Kotlin** + **Spring Boot 3.5.5**, permite simular empréstimos, calculando **parcelas, juros e total a pagar** com base na idade do cliente e nas regras de negócio definidas.

---

## 📊 Regras de Simulação de Crédito

* A **faixa etária do cliente** define a taxa de juros anual:

    * Até 25 anos: 5%
    * 26 a 40 anos: 3%
    * 41 a 60 anos: 2%
    * Acima de 60 anos: 4%

* Fórmula de cálculo da **parcela mensal (PMT)**:

```text
PMT = PV * r / (1 - (1 + r)^-n)
```

* **PV** = Valor do empréstimo
* **r** = Taxa de juros mensal (taxa anual / 12)
* **n** = Número de meses (prazo)
* **Total a pagar** = PMT × n
* **Total de juros** = Total a pagar - PV

---

## 🏛️ Padrão de Arquitetura - Arquitetura Hexagonal

A arquitetura hexagonal, também conhecida como **Ports & Adapters**, organiza o projeto em camadas bem definidas:

1. **Domain (Domínio)**
   Contém **modelos e regras de negócio**, totalmente independente de frameworks, bancos de dados ou APIs externas.

2. **Application (Aplicação)**
   Contém **ports e serviços** que coordenam o fluxo de negócio, sem conhecer detalhes de implementação externa.

3. **Adapters (Adaptadores)**

    * **Inbound:** Recebe as entradas do sistema (ex: APIs REST).
    * **Outbound:** Implementa integrações externas (bancos de dados, envio de e-mails, geração de PDFs).

```
         +----------------+
         | Inbound Adapters |
         | (REST, gRPC)     |
         +--------+---------+
                  |
                  v
         +----------------+
         | Application /  |
         | Ports & Services|
         +--------+-------+
                  |
                  v
         +----------------+
         |     Domain      |
         | (Business Logic)|
         +--------+-------+
                  ^
                  |
         +----------------+
         | Outbound Adapters|
         | (DB, Email, PDF)|
         +----------------+
```

Essa separação promove **alta coesão, baixo acoplamento** e facilita mudanças ou testes em qualquer camada sem afetar o restante do sistema.

---

## 🌟 Benefícios da Escolha

* **Flexibilidade:** Adição de novos canais (REST, gRPC, filas) ou mudança de banco de dados sem tocar no domínio.
* **Testabilidade:** Lógica de negócio isolada, fácil de testar.
* **Manutenção:** Código organizado e compreensível, reduzindo o risco de bugs.
* **Escalabilidade:** Permite crescimento da aplicação sem quebrar camadas existentes.

---

## 📡 Cenário de Mensageria - Fluxo Assíncrono

1. **Cliente → API**
    - O usuário chama `POST /simulate/async`.
    - A API valida a requisição, grava `status = PENDING` no banco e publica a mensagem no broker.

2. **Broker (Kafka/SQS)**
    - Garante que a mensagem será entregue.
    - Suporta múltiplos consumidores (escalabilidade).

3. **Worker de Simulação**
    - Consome mensagens do broker.
    - Executa o motor de cálculo.
    - Atualiza o banco com o resultado (`status = COMPLETED`).
    - Opcionalmente dispara o evento `simulation.completed`.

4. **Notificação ao Cliente**
    - Pode ser **pull** (cliente consulta `GET /simulate/{id}`) ou **push** (webhook, WebSocket, e-mail).

---

# ⚙️ Setup e Configuração do Projeto

## ✅ Requisitos

* Java 17+ instalado (opcional se for usar Docker)
* Maven 3+ instalado (opcional se for usar Docker)
* Docker e Docker Compose (para setup da aplicação)

---

## 📦 Build e Execução da Aplicação

### 1. Build com Maven

```bash
mvn clean package
```

O JAR gerado estará em:

```
target/credit-simulator-0.0.1-SNAPSHOT.jar
```

E pode ser executado com:

```bash
java -jar target/credit-simulator-0.0.1-SNAPSHOT.jar
```

---

### 2. Rodando Testes com Maven

```bash
# Build do projeto
mvn clean install

# Executar testes
mvn test

# Executar aplicação
mvn spring-boot:run
```

---

### 3. Dockerização (QuickStart)

#### a) Estrutura de Pastas Sugerida

```
project-root/
├─ src/
├─ Dockerfile
├─ pom.xml
└─ docker/
   └─ docker-compose.yml
```

#### b) Dockerfile (na raiz do projeto)

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

networks:
  monitoring:

services:
  app:
    build:
      context: ..
      dockerfile: Dockerfile
    image: credit-simulator:latest
    container_name: creditsimulator
    ports:
      - "8080:8080"
    restart: unless-stopped
    networks:
      - monitoring

  postgres:
    image: postgres:15
    container_name: credit_simulator_postgres
    environment:
      POSTGRES_USER: credit_user
      POSTGRES_PASSWORD: credit_pass
      POSTGRES_DB: credit_simulator
    networks:
      - monitoring
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  k6:
    image: grafana/k6
    volumes:
      - ./scripts:/scripts
    command: run /scripts/load_testing_bulk.js
    depends_on:
      - influxdb
      - app
    environment:
      - K6_OUT=influxdb=http://influxdb:8086/k6_credit_simulator
    networks:
      - monitoring

  influxdb:
    image: influxdb:1.8
    container_name: influxdb
    ports:
      - "8086:8086"
    environment:
      - INFLUXDB_DB=k6_credit_simulator
    networks:
      - monitoring

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3000:3000"
    volumes:
      - ./grafana:/etc/grafana/provisioning/
    depends_on:
      - influxdb
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=admin
    networks:
      - monitoring

volumes:
  postgres_data:
```

#### d) Rodando a Aplicação com Docker Compose

```bash
docker-compose -f docker/docker-compose.yml up --build
```

A aplicação estará disponível em: [http://localhost:8080](http://localhost:8080)

Para parar:

```bash
docker-compose -f docker/docker-compose.yml down
```
## 📖 Documentação da API

A documentação interativa da API está disponível em:

- [Swagger UI](http://localhost:8080/swagger-ui.html)
- [OpenAPI JSON](http://localhost:8080/v3/api-docs)

## 🚀 Como acessar a documentação

Após iniciar a aplicação, a documentação estará disponível em:

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Esquema OpenAPI (JSON): [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
---

## 🧪 Testes de Carga (Alto Volume)

* Os **scripts do k6** estão em: `docker/scripts/load_testing_bulk.js`
* Configuração de **monitoramento** com Grafana + InfluxDB.

Rode o comando do docker compose do k6 para rodar o teste de carga

```bash
docker compose up k6 -d
```

## 📊 Visualização do Dashboard no Grafana

* O Grafana estará rodando na porta **:3000** após subir os containers.
* Acesse o painel em [http://localhost:3000](http://localhost:3000) com usuário e senha padrão:  
  **usuário:** `admin`  
  **senha:** `admin`
  > Obs: será necessário definir uma nova senha no primeiro login.

---

### 🔧 Configuração Inicial

1. **Criar a conexão com o InfluxDB**
    - Vá até **Configuration → Data Sources**.
    - Clique em **Add data source**.
    - Selecione **InfluxDB**.
    - Configure os campos principais:
        - **URL:** `http://influxdb:8086`  
          (ou `http://localhost:8086` se o Grafana estiver rodando fora do Docker)
        - **Database:** `k6`
        - **User:** `admin` (se configurado)
        - **Password:** `admin` (se configurado)
    - Clique em **Save & Test** para validar a conexão.

2. **Importar o Dashboard de Load Testing**
    - Vá até **Create → Import** no menu lateral do Grafana.
    - No campo **Import via grafana.com**, insira o ID do dashboard:  
      **2587**  
      ou acesse direto: [k6 Load Testing Results](https://grafana.com/grafana/dashboards/2587-k6-load-testing-results/).
    - Clique em **Load**.
    - Escolha o **Data Source** criado (InfluxDB).
    - Clique em **Import** para finalizar.

3. **Explorar os resultados**
    - Após rodar os testes com o k6, os dados serão enviados automaticamente para o InfluxDB.
    - O dashboard importado mostrará métricas de:
        - VUs ativos (Virtual Users)
        - Taxa de requisições por segundo
        - Latência (p95, p99)
        - Erros e falhas durante o teste

---

✅ Agora o Grafana exibirá em tempo real os resultados de carga gerados pelo k6, facilitando o acompanhamento e análise de performance.


## 🏗️ Tecnologias Utilizadas

- **Kotlin + Spring Boot** → desenvolvimento da API e dos consumidores (workers).
- **PostgreSQL** → persistência de simulações e resultados.
- **Grafana + InfluxDB** → observabilidade e visualização de métricas de execução.
- **Grafana k6** → testes de carga e performance integrados ao pipeline.
- **ktlint** → verificação automática de estilo e formatação do código Kotlin.
- **Detekt** → análise estática para detectar potenciais problemas e más práticas em Kotlin.


## ❤️ Desenvolvido com Kotlin + Spring Boot

[![Swagger](https://img.shields.io/badge/API%20Docs-Swagger-green)](http://localhost:8080/swagger-ui.html)