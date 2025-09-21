import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Counter } from 'k6/metrics';

// Métricas customizadas
export const options = {
  vus: 1000,                  // 50 usuários virtuais
  duration: '10s',          // duração total do teste
  thresholds: {
    'http_req_duration': ['p(95)<500', 'avg<300'],  // latência
    'http_req_failed': ['rate<0.01'],               // até 1% de falhas
    'errors': ['rate<0.01'],
    'data_received': ['count>0'],
    'http_reqs': ['count>100'],
  },
  summaryTrendStats: ['avg', 'p(90)', 'p(95)', 'p(99)', 'min', 'max'],
};

export default function () {
  const url = 'http://creditsimulator:8080/api/loans/simulate/bulk'; // serviço "app" no docker-compose

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

  const params = {
    headers: { 'Content-Type': 'application/json' },
  };

  let res = http.post(url, generateSimulations(5), params);

check(res, { 'status é 200': (r) => r.status === 200 }) || (() => {
  errorRate.add(1);
  console.log(`❌ Erro status=${res.status} body=${res.body}`);
})();

  sleep(1);
}