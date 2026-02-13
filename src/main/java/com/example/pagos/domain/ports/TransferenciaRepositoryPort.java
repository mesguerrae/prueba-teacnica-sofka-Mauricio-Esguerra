package com.example.pagos.domain.ports;

import com.example.pagos.domain.model.Transferencia;

import java.util.Optional;
import java.util.UUID;

public interface TransferenciaRepositoryPort {

    Transferencia guardar(Transferencia transferencia);

    Optional<Transferencia> obtenerPorId(UUID id);
}

