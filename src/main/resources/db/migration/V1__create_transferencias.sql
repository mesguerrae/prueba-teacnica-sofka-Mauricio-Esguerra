CREATE TABLE transferencias (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    banco_origen VARCHAR(20) NOT NULL,
    banco_destino VARCHAR(20) NOT NULL,
    cuenta_origen VARCHAR(50) NOT NULL,
    cuenta_destino VARCHAR(50) NOT NULL,
    monto DECIMAL(18,2) NOT NULL,
    moneda VARCHAR(10) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    creada_en DATETIMEOFFSET NOT NULL,
    actualizada_en DATETIMEOFFSET NULL,
    motivo_falla VARCHAR(255) NULL
);

