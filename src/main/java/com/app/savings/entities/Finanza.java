package com.app.savings.entities;

import java.time.LocalDate;
import com.app.savings.enums.MedioPago;
import com.app.savings.enums.TipoFinanza;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Finanza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String concepto;
    private LocalDate fecha;
    private double cantidad;

    @Enumerated(EnumType.STRING)
    private TipoFinanza tipo;

    @Enumerated(EnumType.STRING)
    private MedioPago medio;

    private String file;

    @ManyToOne
    private Usuario usuario;
}
