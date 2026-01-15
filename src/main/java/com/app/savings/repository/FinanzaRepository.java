package com.app.savings.repository;

import com.app.savings.entities.Finanza;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FinanzaRepository extends JpaRepository<Finanza, Long> {
    List<Finanza> findByUsuarioUsername(String username);
}
