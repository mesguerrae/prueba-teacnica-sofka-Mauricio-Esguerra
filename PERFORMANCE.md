# PERFORMANCE.md

Este documento describe a alto nivel cómo validaría que el microservicio soporta al menos **30 TPS (transacciones por segundo)** de forma sostenida.

## Objetivo

- Verificar que el servicio puede procesar 30 TPS con latencias aceptables (p95 < 500 ms para llamadas que no dependen del banco externo).
- Validar que bajo carga el uso de CPU/memoria sigue dentro de los límites definidos en Kubernetes.

## Estrategia de prueba de carga

- **Herramientas**:
  - `k6` para generar carga HTTP.
  - `Grafana` + `Prometheus` (o Azure Monitor / CloudWatch) para observabilidad.
- **Escenario principal**:
  - Endpoint: `POST /api/transferencias`
  - Payloads variados de bancos destino (incluyendo uno con latencia alta de Mock/Wiremock).

### Ejemplo de script k6 (pseudo-código)

1. Configurar el objetivo:
   - 5 minutos de rampa hasta 30 virtual users.
   - 10 minutos manteniendo 30 virtual users (aprox. 30 TPS si cada usuario hace 1 req/s).
2. Medir:
   - `http_req_duration` (p50, p90, p95, p99).
   - Ratio de errores HTTP (4xx/5xx).
3. Separar métricas para:
   - Bancos con respuesta rápida (mock rápido).
   - Banco con respuesta lenta (mock con delay 15s), para validar que los timeouts y circuit breakers protegen al resto.

### Métricas a observar

- Latencias por endpoint.
- Uso de CPU/memoria en pods de `pagos-interbancarios`.
- Número de timeouts / reintentos del cliente al banco externo (métricas de Resilience4j).
- Crecimiento de conexiones a la base de datos y tiempo de respuesta promedio en SQL Server.

## Criterios de éxito

- Se sostienen **≥30 TPS** durante el período de carga sin incremento progresivo de latencias.
- p95 de `http_req_duration` < 500 ms para bancos sin latencias artificiales.
- Tasa de errores < 1% en condiciones nominales.
- No hay saturación de CPU/memoria ni errores de falta de conexiones a SQL Server.

