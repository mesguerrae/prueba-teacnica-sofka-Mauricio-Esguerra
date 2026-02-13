package com.example.pagos.infrastructure.persistence;

import com.example.pagos.domain.model.Transferencia;
import com.example.pagos.domain.ports.TransferenciaRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TransferenciaRepositoryAdapter implements TransferenciaRepositoryPort {

    private final TransferenciaJpaRepository jpaRepository;

    public TransferenciaRepositoryAdapter(TransferenciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transferencia guardar(Transferencia transferencia) {
        TransferenciaEntity entity = toEntity(transferencia);
        TransferenciaEntity guardada = jpaRepository.save(entity);
        return toDomain(guardada);
    }

    @Override
    public Optional<Transferencia> obtenerPorId(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private TransferenciaEntity toEntity(Transferencia transferencia) {
        TransferenciaEntity entity = new TransferenciaEntity();
        entity.setId(transferencia.getId());
        entity.setBancoOrigen(transferencia.getBancoOrigen());
        entity.setBancoDestino(transferencia.getBancoDestino());
        entity.setCuentaOrigen(transferencia.getCuentaOrigen());
        entity.setCuentaDestino(transferencia.getCuentaDestino());
        entity.setMonto(transferencia.getMonto());
        entity.setMoneda(transferencia.getMoneda());
        entity.setEstado(transferencia.getEstado().name());
        entity.setCreadaEn(transferencia.getCreadaEn());
        entity.setActualizadaEn(transferencia.getActualizadaEn());
        entity.setMotivoFalla(transferencia.getMotivoFalla());
        return entity;
    }

    private Transferencia toDomain(TransferenciaEntity entity) {
        return new Transferencia(
                entity.getId(),
                entity.getBancoOrigen(),
                entity.getBancoDestino(),
                entity.getCuentaOrigen(),
                entity.getCuentaDestino(),
                entity.getMonto(),
                entity.getMoneda(),
                Transferencia.Estado.valueOf(entity.getEstado()),
                entity.getCreadaEn(),
                entity.getActualizadaEn(),
                entity.getMotivoFalla()
        );
    }
}

