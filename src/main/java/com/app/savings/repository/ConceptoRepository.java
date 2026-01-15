package com.app.savings.repository;

import com.app.savings.entities.Concepto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConceptoRepository extends JpaRepository<Concepto, Long> {
    java.util.Optional<Concepto> findByNombre(String nombre);
}
