package com.example.pagos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransferenciaJpaRepository extends JpaRepository<TransferenciaEntity, UUID> {
}

