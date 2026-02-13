package com.example.pagos.infrastructure.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferenciaRequest(
        @NotBlank String bancoOrigen,
        @NotBlank String bancoDestino,
        @NotBlank String cuentaOrigen,
        @NotBlank String cuentaDestino,
        @NotNull @Min(1) BigDecimal monto,
        @NotBlank String moneda
) {
}

