package com.example.pagos.infrastructure.bank;

import com.example.pagos.domain.model.Transferencia;

public interface BancoDestinoStrategy {

    void enviar(Transferencia transferencia);
}

