package com.app.savings.repository;

import com.app.savings.entities.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PerfilUsuarioRepository extends JpaRepository<PerfilUsuario, Long> {
    Optional<PerfilUsuario> findByNombre(String nombre);
}
