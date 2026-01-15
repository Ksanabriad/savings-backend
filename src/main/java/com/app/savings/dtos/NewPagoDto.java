package com.app.savings.dtos;

import java.time.LocalDate;

import com.app.savings.enums.TipoFinanza;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPagoDto {

    private Double cantidad;
    private TipoFinanza tipoFinanza;
    private LocalDate fecha;
    private String codigoEstudiante;

}
