package com.example.pagos.domain.ports;

import com.example.pagos.domain.model.Transferencia;

public interface BancoDestinoClientPort {

    void procesarTransferencia(Transferencia transferencia);
}

