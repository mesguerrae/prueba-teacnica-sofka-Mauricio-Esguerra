package com.example.pagos.application;

import com.example.pagos.domain.model.Transferencia;
import com.example.pagos.domain.ports.BancoDestinoClientPort;
import com.example.pagos.domain.ports.TransferenciaRepositoryPort;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class ProcesarTransferenciaService {

    private final TransferenciaRepositoryPort transferenciaRepository;
    private final BancoDestinoClientPort bancoDestinoClient;

    public ProcesarTransferenciaService(TransferenciaRepositoryPort transferenciaRepository,
                                        BancoDestinoClientPort bancoDestinoClient) {
        this.transferenciaRepository = transferenciaRepository;
        this.bancoDestinoClient = bancoDestinoClient;
    }

    public UUID procesar(ProcesarTransferenciaCommand command) {
        validarMonto(command.monto());

        Transferencia transferencia = new Transferencia(
                UUID.randomUUID(),
                command.bancoOrigen(),
                command.bancoDestino(),
                command.cuentaOrigen(),
                command.cuentaDestino(),
                command.monto(),
                command.moneda(),
                Transferencia.Estado.PENDIENTE,
                OffsetDateTime.now(),
                null,
                null
        );

        // Persistimos en estado PENDIENTE
        transferenciaRepository.guardar(transferencia);

        try {
            // Llamamos al banco externo (Mock) mediante el puerto
            bancoDestinoClient.procesarTransferencia(transferencia);
            transferencia.marcarExitosa();
        } catch (RuntimeException ex) {
            transferencia.marcarFallida(ex.getMessage());
            throw ex;
        } finally {
            // Actualizamos el estado final
            transferenciaRepository.guardar(transferencia);
        }

        return transferencia.getId();
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
    }
}

