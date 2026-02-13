package com.example.pagos.application;

import com.example.pagos.domain.model.Transferencia;
import com.example.pagos.domain.ports.BancoDestinoClientPort;
import com.example.pagos.domain.ports.TransferenciaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProcesarTransferenciaServiceTest {

    private TransferenciaRepositoryPort repositoryPort;
    private BancoDestinoClientPort bancoDestinoClientPort;
    private ProcesarTransferenciaService service;

    @BeforeEach
    void setUp() {
        repositoryPort = mock(TransferenciaRepositoryPort.class);
        bancoDestinoClientPort = mock(BancoDestinoClientPort.class);
        service = new ProcesarTransferenciaService(repositoryPort, bancoDestinoClientPort);

        when(repositoryPort.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(repositoryPort.obtenerPorId(any(UUID.class))).thenReturn(Optional.empty());
    }

    @Test
    void deberiaProcesarTransferenciaExitosa() {
        ProcesarTransferenciaCommand command = new ProcesarTransferenciaCommand(
                "BANCO_CENTRAL",
                "BANCO_COLOMBIA",
                "123",
                "456",
                BigDecimal.valueOf(100),
                "COP"
        );

        UUID id = service.procesar(command);

        assertNotNull(id);
        verify(repositoryPort, atLeast(2)).guardar(any(Transferencia.class));
        verify(bancoDestinoClientPort, times(1)).procesarTransferencia(any(Transferencia.class));
    }

    @Test
    void deberiaFallarSiMontoNoEsValido() {
        ProcesarTransferenciaCommand command = new ProcesarTransferenciaCommand(
                "BANCO_CENTRAL",
                "BANCO_FALLA",
                "123",
                "456",
                BigDecimal.ZERO,
                "COP"
        );

        assertThrows(IllegalArgumentException.class, () -> service.procesar(command));
        verifyNoInteractions(bancoDestinoClientPort);
    }
}

