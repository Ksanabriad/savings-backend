package com.app.savings.repository;

import com.app.savings.entities.Finanza;
import com.app.savings.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.app.savings.dtos.MonthlyExpenseDto;

import java.util.List;

public interface FinanzaRepository extends JpaRepository<Finanza, Long> {
        List<Finanza> findByUsuarioUsername(String username);

        @Query("SELECT f FROM Finanza f WHERE f.usuario = :usuario AND YEAR(f.fecha) = :anio AND MONTH(f.fecha) = :mes")
        List<Finanza> findByUsuarioAndMesAndAnio(@Param("usuario") Usuario usuario, @Param("mes") int mes,
                        @Param("anio") int anio);

        Finanza findTopByUsuarioOrderByFechaAsc(Usuario usuario);

        @Query("SELECT SUM(f.cantidad) FROM Finanza f WHERE f.usuario.username = :username AND f.tipo.nombre = :tipo")
        Double sumByUsuarioAndTipo(@Param("username") String username, @Param("tipo") String tipo);

        @Query("SELECT new com.app.savings.dtos.MonthlyExpenseDto(EXTRACT(YEAR FROM f.fecha), EXTRACT(MONTH FROM f.fecha), SUM(f.cantidad)) "
                        +
                        "FROM Finanza f " +
                        "WHERE f.usuario.username = :username AND f.tipo.nombre = 'EGRESO' " +
                        "AND f.fecha >= :startDate AND f.fecha < :endDate " +
                        "GROUP BY EXTRACT(YEAR FROM f.fecha), EXTRACT(MONTH FROM f.fecha) " +
                        "ORDER BY EXTRACT(YEAR FROM f.fecha) DESC, EXTRACT(MONTH FROM f.fecha) DESC")
        List<MonthlyExpenseDto> findMonthlyExpenses(@Param("username") String username,
                        @Param("startDate") java.time.LocalDate startDate,
                        @Param("endDate") java.time.LocalDate endDate);

        @Query("SELECT new com.app.savings.dtos.MonthlyExpenseDto(EXTRACT(YEAR FROM f.fecha), EXTRACT(MONTH FROM f.fecha), SUM(f.cantidad)) "
                        +
                        "FROM Finanza f " +
                        "WHERE f.usuario.username = :username AND f.tipo.nombre = 'INGRESO' " +
                        "AND f.fecha >= :startDate AND f.fecha < :endDate " +
                        "GROUP BY EXTRACT(YEAR FROM f.fecha), EXTRACT(MONTH FROM f.fecha) " +
                        "ORDER BY EXTRACT(YEAR FROM f.fecha) DESC, EXTRACT(MONTH FROM f.fecha) DESC")
        List<com.app.savings.dtos.MonthlyExpenseDto> findMonthlyIncome(@Param("username") String username,
                        @Param("startDate") java.time.LocalDate startDate,
                        @Param("endDate") java.time.LocalDate endDate);

        @Query("SELECT SUM(f.cantidad) FROM Finanza f WHERE f.usuario.username = :username AND f.tipo.nombre = :tipo AND f.fecha >= :startDate AND f.fecha < :endDate")
        Double sumByUsuarioAndTipoAndDateRange(@Param("username") String username, @Param("tipo") String tipo,
                        @Param("startDate") java.time.LocalDate startDate,
                        @Param("endDate") java.time.LocalDate endDate);

        @Query(value = "SELECT c.nombre AS category, SUM(f.cantidad) AS total FROM Finanza f " +
                        "JOIN f.concepto c " +
                        "WHERE f.usuario.username = :username AND f.tipo.nombre = 'EGRESO' " +
                        "AND f.fecha >= :startDate AND f.fecha < :endDate " +
                        "GROUP BY c.nombre")
        List<Object[]> findCategoryDistributionRaw(@Param("username") String username,
                        @Param("startDate") java.time.LocalDate startDate,
                        @Param("endDate") java.time.LocalDate endDate);

        default List<java.util.Map<String, Object>> findCategoryDistribution(String username,
                        java.time.LocalDate startDate,
                        java.time.LocalDate endDate) {
                return findCategoryDistributionRaw(username, startDate, endDate).stream()
                                .map(row -> java.util.Map.of("concepto", row[0], "total", row[1]))
                                .collect(java.util.stream.Collectors.toList());
        }

        @Query("SELECT f FROM Finanza f WHERE f.usuario.username = :username ORDER BY f.fecha DESC LIMIT :limit")
        List<Finanza> findTopByUsuarioUsernameOrderByFechaDesc(@Param("username") String username,
                        @Param("limit") int limit);
}
