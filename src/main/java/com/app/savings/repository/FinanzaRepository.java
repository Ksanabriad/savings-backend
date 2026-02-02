package com.app.savings.repository;

import com.app.savings.entities.Finanza;
import com.app.savings.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FinanzaRepository extends JpaRepository<Finanza, Long> {
    List<Finanza> findByUsuarioUsername(String username);

    @Query("SELECT f FROM Finanza f WHERE f.usuario = :usuario AND YEAR(f.fecha) = :anio AND MONTH(f.fecha) = :mes")
    List<Finanza> findByUsuarioAndMesAndAnio(@Param("usuario") Usuario usuario, @Param("mes") int mes,
            @Param("anio") int anio);

    Finanza findTopByUsuarioOrderByFechaAsc(Usuario usuario);
}
