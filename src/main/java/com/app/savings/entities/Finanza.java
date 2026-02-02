package com.app.savings.entities;

import java.time.LocalDate;
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

    @ManyToOne
    private Concepto concepto;
    private LocalDate fecha;
    private double cantidad;

    @ManyToOne
    private TipoFinanza tipo;

    @ManyToOne
    private MedioPago medio;

    private String file;

    @ManyToOne
    private Usuario usuario;
}
