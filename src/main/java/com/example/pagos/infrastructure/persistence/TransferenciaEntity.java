package com.example.pagos.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "transferencias")
public class TransferenciaEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(name = "banco_origen", nullable = false, length = 20)
    private String bancoOrigen;

    @Column(name = "banco_destino", nullable = false, length = 20)
    private String bancoDestino;

    @Column(name = "cuenta_origen", nullable = false, length = 50)
    private String cuentaOrigen;

    @Column(name = "cuenta_destino", nullable = false, length = 50)
    private String cuentaDestino;

    @Column(name = "monto", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @Column(name = "moneda", nullable = false, length = 10)
    private String moneda;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "creada_en", nullable = false)
    private OffsetDateTime creadaEn;

    @Column(name = "actualizada_en")
    private OffsetDateTime actualizadaEn;

    @Column(name = "motivo_falla", length = 255)
    private String motivoFalla;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBancoOrigen() {
        return bancoOrigen;
    }

    public void setBancoOrigen(String bancoOrigen) {
        this.bancoOrigen = bancoOrigen;
    }

    public String getBancoDestino() {
        return bancoDestino;
    }

    public void setBancoDestino(String bancoDestino) {
        this.bancoDestino = bancoDestino;
    }

    public String getCuentaOrigen() {
        return cuentaOrigen;
    }

    public void setCuentaOrigen(String cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public OffsetDateTime getCreadaEn() {
        return creadaEn;
    }

    public void setCreadaEn(OffsetDateTime creadaEn) {
        this.creadaEn = creadaEn;
    }

    public OffsetDateTime getActualizadaEn() {
        return actualizadaEn;
    }

    public void setActualizadaEn(OffsetDateTime actualizadaEn) {
        this.actualizadaEn = actualizadaEn;
    }

    public String getMotivoFalla() {
        return motivoFalla;
    }

    public void setMotivoFalla(String motivoFalla) {
        this.motivoFalla = motivoFalla;
    }
}

