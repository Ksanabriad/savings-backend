package com.app.savings.repository;

import com.app.savings.entities.MedioPago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MedioPagoRepository extends JpaRepository<MedioPago, Long> {
    Optional<MedioPago> findByNombre(String nombre);
}
