package com.example.pagos.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Transferencia {

    public enum Estado {
        PENDIENTE,
        EXITOSA,
        FALLIDA
    }

    private final UUID id;
    private final String bancoOrigen;
    private final String bancoDestino;
    private final String cuentaOrigen;
    private final String cuentaDestino;
    private final BigDecimal monto;
    private final String moneda;
    private Estado estado;
    private final OffsetDateTime creadaEn;
    private OffsetDateTime actualizadaEn;
    private String motivoFalla;

    public Transferencia(UUID id,
                         String bancoOrigen,
                         String bancoDestino,
                         String cuentaOrigen,
                         String cuentaDestino,
                         BigDecimal monto,
                         String moneda,
                         Estado estado,
                         OffsetDateTime creadaEn,
                         OffsetDateTime actualizadaEn,
                         String motivoFalla) {
        this.id = id;
        this.bancoOrigen = bancoOrigen;
        this.bancoDestino = bancoDestino;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.monto = monto;
        this.moneda = moneda;
        this.estado = estado;
        this.creadaEn = creadaEn;
        this.actualizadaEn = actualizadaEn;
        this.motivoFalla = motivoFalla;
    }

    public UUID getId() {
        return id;
    }

    public String getBancoOrigen() {
        return bancoOrigen;
    }

    public String getBancoDestino() {
        return bancoDestino;
    }

    public String getCuentaOrigen() {
        return cuentaOrigen;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getMoneda() {
        return moneda;
    }

    public Estado getEstado() {
        return estado;
    }

    public OffsetDateTime getCreadaEn() {
        return creadaEn;
    }

    public OffsetDateTime getActualizadaEn() {
        return actualizadaEn;
    }

    public String getMotivoFalla() {
        return motivoFalla;
    }

    public void marcarExitosa() {
        this.estado = Estado.EXITOSA;
        this.actualizadaEn = OffsetDateTime.now();
        this.motivoFalla = null;
    }

    public void marcarFallida(String motivo) {
        this.estado = Estado.FALLIDA;
        this.actualizadaEn = OffsetDateTime.now();
        this.motivoFalla = motivo;
    }
}

