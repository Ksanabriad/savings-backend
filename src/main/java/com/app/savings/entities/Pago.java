package com.app.savings.entities;

import java.time.LocalDate;

import com.app.savings.enums.PagoStatus;
import com.app.savings.enums.TypePago;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private double cantidad;
    private TypePago type;
    private PagoStatus status;
    private String file;

    //Un estudiante puede tener muchos pagos
    @ManyToOne
    private Estudiante estudiante;
}
