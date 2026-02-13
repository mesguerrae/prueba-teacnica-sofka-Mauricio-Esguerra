## Pagos Interbancarios - Orquestador

Microservicio de alta disponibilidad que actúa como puente entre el Procesador Central de Pagos y los Nodos Bancarios de Colombia. Implementa **Arquitectura Hexagonal**, con capas de dominio, aplicación e infraestructura.

### Tecnologías

- **Java 17**
- **Spring Boot 3**
- **Spring Web / Spring Data JPA / Validation**
- **SQL Server** (persistencia)
- **Flyway** (migraciones)
- **Resilience4j** (timeouts y reintentos)
- **Docker / Docker Compose / Kubernetes**

---

## Estructura del proyecto

- `domain`:
  - `model.Transferencia`
  - `ports.TransferenciaRepositoryPort`
  - `ports.BancoDestinoClientPort`
- `application`:
  - `ProcesarTransferenciaCommand`
  - `ProcesarTransferenciaService`
- `infrastructure`:
  - `api.TransferenciaController`
  - `persistence.TransferenciaEntity`, `TransferenciaJpaRepository`, `TransferenciaRepositoryAdapter`
  - `bank.BancoDestinoStrategy`, `MockBancoColombiaStrategy`, `MockBancoFallidoStrategy`, `BancoDestinoClientAdapter`
  - `config.ApplicationConfig`

---

## Ejecución local

### Requisitos

- Java 17
- Maven 3.9+
- Docker + Docker Compose

### Compilación y pruebas

```bash
mvn clean install
```

### Migraciones de base de datos

El proyecto incluye una migración inicial en `src/main/resources/db/migration/V1__create_transferencias.sql` que crea la tabla `transferencias`.

- **En entorno local con Docker Compose (config actual)**:
  - Para simplificar compatibilidad de versiones, Flyway está **deshabilitado** en `application.yml` y JPA crea la tabla automáticamente (`spring.jpa.hibernate.ddl-auto=update`).
  - Si quieres aplicar la migración SQL explícitamente:
    1. Conéctate al SQL Server del `docker-compose` (`localhost,1433`, usuario `sa`, password `YourStrong!Passw0rd`).
    2. En SSMS / Azure Data Studio ejecuta el contenido de `V1__create_transferencias.sql` sobre la base que estés usando (por defecto `master`).

- **En un entorno más real (con Flyway habilitado)**:
  - Cambia en `application.yml`:
    - `spring.flyway.enabled: true`
    - Ajusta `spring.datasource.url` para apuntar a la base de negocio (por ejemplo `databaseName=pagosdb`).
  - Al arrancar la aplicación (`mvn spring-boot:run` o el contenedor Docker), Flyway detectará las migraciones pendientes en `db/migration` y las aplicará automáticamente.

### Levantar todo el ecosistema con Docker Compose

```bash
docker compose up --build
```

Servicios:

- `pagos-interbancarios`: `http://localhost:8080`
- `sqlserver`: `localhost:1433`
- `mock-banco-colombia` (Wiremock): `http://localhost:8081`

### Endpoint principal

- `POST /api/transferencias`

Ejemplo de request **EXITOSO (BANCO_COLOMBIA)**:

```json
{
  "bancoOrigen": "BANCO_CENTRAL",
  "bancoDestino": "BANCO_COLOMBIA",
  "cuentaOrigen": "1234567890",
  "cuentaDestino": "0987654321",
  "monto": 100000.50,
  "moneda": "COP"
}
```

Respuesta (202 Accepted):

```json
{
  "id": "uuid-de-la-transferencia",
  "estado": "PENDIENTE"
}
```

Ejemplo de request **FALLIDO (BANCO_FALLA)**:

```json
{
  "bancoOrigen": "BANCO_CENTRAL",
  "bancoDestino": "BANCO_FALLA",
  "cuentaOrigen": "1234567890",
  "cuentaDestino": "0987654321",
  "monto": 100000.50,
  "moneda": "COP"
}
```

En este caso se dispara la estrategia `MockBancoFallidoStrategy`, se lanzan reintentos controlados y la transferencia termina en estado **FALLIDA** en la tabla `transferencias`.

---

## Despliegue en Kubernetes

Manifiestos en `k8s/`:

- `deployment.yaml`
- `service.yaml`

Pasos básicos:

1. Construir y publicar imagen:

```bash
docker build -t your-docker-registry/pagos-interbancarios:latest .
docker push your-docker-registry/pagos-interbancarios:latest
```

2. Crear secreto con credenciales de SQL Server:

```bash
kubectl create secret generic sqlserver-credentials \
  --from-literal=username=sa \
  --from-literal=password=YourStrong!Passw0rd
```

3. Aplicar manifiestos:

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

---

## Diagrama de flujo de la solución

Representación simplificada (texto):

1. Cliente llama `POST /api/transferencias`.
2. `TransferenciaController`:
   - Valida el request (Bean Validation).
   - Crea `ProcesarTransferenciaCommand`.
3. `ProcesarTransferenciaService`:
   - Valida reglas de dominio (ej. monto > 0).
   - Construye `Transferencia` en estado **PENDIENTE**.
   - Persiste en DB vía `TransferenciaRepositoryPort`.
4. `BancoDestinoClientPort`:
   - Selecciona `BancoDestinoStrategy` según `bancoDestino`.
   - Llama al banco externo (mock) con **timeout + reintentos** (Resilience4j).
5. Según respuesta:
   - Marca `Transferencia` como **EXITOSA** o **FALLIDA**.
   - Vuelve a persistir el nuevo estado.

---

## Diagrama de secuencia (simplificado)

Texto tipo UML:

1. `Cliente` → `TransferenciaController` : `POST /api/transferencias`
2. `TransferenciaController` → `ProcesarTransferenciaService` : `procesar(command)`
3. `ProcesarTransferenciaService` → `TransferenciaRepositoryPort` : `guardar(transferencia PENDIENTE)`
4. `ProcesarTransferenciaService` → `BancoDestinoClientPort` : `procesarTransferencia(transferencia)`
5. `BancoDestinoClientPort` → `BancoDestinoStrategy` : `enviar(transferencia)` (HTTP → Banco externo Mock)
6. `BancoDestinoStrategy` → `BancoDestinoClientPort` : resultado (éxito / error / timeout)
7. `BancoDestinoClientPort` → `ProcesarTransferenciaService` : retorno (éxito o excepción)
8. `ProcesarTransferenciaService`:
   - Si éxito: `transferencia.marcarExitosa()`
   - Si error: `transferencia.marcarFallida(...)`
9. `ProcesarTransferenciaService` → `TransferenciaRepositoryPort` : `guardar(transferencia actualizada)`
10. `ProcesarTransferenciaService` → `TransferenciaController` : `UUID`
11. `TransferenciaController` → `Cliente` : `202 Accepted (id, estado=PENDIENTE)`

---

## MER (Modelo Entidad-Relación)

Entidad principal:

- **TRANSFERENCIA**
  - `id` (PK, UNIQUEIDENTIFIER)
  - `banco_origen` (VARCHAR(20))
  - `banco_destino` (VARCHAR(20))
  - `cuenta_origen` (VARCHAR(50))
  - `cuenta_destino` (VARCHAR(50))
  - `monto` (DECIMAL(18,2))
  - `moneda` (VARCHAR(10))
  - `estado` (VARCHAR(20)) — valores lógicos: `PENDIENTE`, `EXITOSA`, `FALLIDA`
  - `creada_en` (DATETIMEOFFSET)
  - `actualizada_en` (DATETIMEOFFSET)
  - `motivo_falla` (VARCHAR(255))

Relaciones:

- El modelo propuesto asume una sola entidad de `TRANSFERENCIA` (el resto de entidades — bancos, cuentas — pueden residir en otros sistemas maestros).

---

## Preguntas de diseño

### 1. Si el banco externo tarda 15 segundos en responder, ¿cómo evitas que el servicio degrade su rendimiento para otros bancos?

- **Respuesta**:
  - El cliente hacia el banco externo está envuelto con **Resilience4j TimeLimiter y Retry**, con un timeout de **2 segundos** y un número limitado de reintentos.
  - Si un banco específico (por ejemplo, `BANCO_COLOMBIA`) empieza a tardar 15 segundos, el timeout corta la llamada alrededor de los 2 segundos y marca la transferencia como **FALLIDA** tras agotar los reintentos.
  - Esto evita que los hilos del pool de Tomcat (o del servidor embebido) queden bloqueados 15 segundos; en lugar de eso, la degradación se aísla a ese banco.
  - Adicionalmente, se puede complementar con **Bulkhead/ThreadPoolBulkhead** y **Circuit Breaker** por banco destino, de forma que:
    - Se limite explícitamente el número de llamadas concurrentes a cada banco.
    - Si un banco tiene muchos fallos consecutivos, el circuito se abre y se responde rápido con error local, protegiendo a bancos sanos.

### 2. ¿Cómo garantizamos la integridad de la transacción si el servicio se cae justo después de que el banco destino confirmó el pago, pero antes de guardar en la DB local?

- **Respuesta**:
  - El diseño actual persiste primero la transferencia en estado **PENDIENTE**, luego llama al banco externo, actualiza el estado y vuelve a persistir.
  - En el escenario descrito, existe un **riesgo de desalineación** (banco destino confirmó, pero nuestra base no refleja `EXITOSA`).
  - Para mitigarlo, la estrategia recomendada es:
    - Asumir que el estado de verdad es el del **banco destino**.
    - Implementar un mecanismo de **reconciliación/compensación**:
      - Un proceso asíncrono (job o cola) que consulta periódicamente el estado de operaciones en el banco destino cuando:
        - La transferencia está en DB local como `PENDIENTE`.
        - O hay errores de comunicación justo después de un supuesto éxito.
      - Este proceso actualiza posteriormente la transferencia local a `EXITOSA` o `FALLIDA` según la confirmación del banco.
    - Opcionalmente, usar un **Outbox pattern**:
      - Registrar en la DB local eventos de integración y procesarlos de forma asíncrona y transaccionalmente consistente con el cambio de estado.
  - Con esta combinación (persistir siempre `PENDIENTE` + reconciliación periódica + posible outbox/eventos), minimizamos la ventana de inconsistencia y tenemos un camino claro para corregirla.

---

## PERFORMANCE

Consulta `PERFORMANCE.md` para el detalle de la estrategia de pruebas de carga y métricas objetivo (≥30 TPS).

