package com.app.savings.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialInforme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaGeneracion;
    private String nombreArchivo;

    @ManyToOne
    private Usuario usuario;

    @OneToMany
    private List<Finanza> finanzas;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] contenido;
}
