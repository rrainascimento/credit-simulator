import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: 1000,              // apenas 1 usuário virtual, pq o foco é volume no body
  duration: '5s',       // executa só 1 vez
  thresholds: {
    http_req_duration: ["p(95)<8000"], // 95% das req devem responder em < 5s
    http_req_failed: ["rate<0.01"],    // < 1% de falhas
  },
};

// função auxiliar p/ gerar payloads fake
function generateSimulations(count) {
  const sims = [];
  for (let i = 0; i < count; i++) {
    sims.push({
      loanAmount: Math.floor(Math.random() * 100000) + 1000, // entre 1k e 100k
      birthDate: "1990-05-20",
      months: Math.floor(Math.random() * 60) + 12, // 12 a 72 meses
    });
  }
  return sims;
}

export default function () {
  const url = "http://creditsimulator:8080/api/loans/simulate/bulk";

  // gera 10.000 simulações em memória
  const payload = JSON.stringify(generateSimulations(1));
  console.log(payload);

  const params = {
    headers: { "Content-Type": "application/json" },
  };

  const res = http.post(url, payload, params);

  check(res, {
    "status is 200": (r) => r.status === 200,
    "response time OK": (r) => r.timings.duration < 5000,
  });

  sleep(1);
}
