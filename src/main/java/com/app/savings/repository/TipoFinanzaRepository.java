package com.app.savings.repository;

import com.app.savings.entities.TipoFinanza;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TipoFinanzaRepository extends JpaRepository<TipoFinanza, Long> {
    Optional<TipoFinanza> findByNombre(String nombre);
}
