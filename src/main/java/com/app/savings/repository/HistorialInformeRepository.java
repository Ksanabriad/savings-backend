package com.app.savings.repository;

import com.app.savings.entities.HistorialInforme;
import com.app.savings.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialInformeRepository extends JpaRepository<HistorialInforme, Long> {
    List<HistorialInforme> findByUsuario(Usuario usuario);

    boolean existsByUsuarioAndNombreArchivo(Usuario usuario, String nombreArchivo);
}
