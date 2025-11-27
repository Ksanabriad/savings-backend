package com.app.savings.dtos;

import java.time.LocalDate;

import com.app.savings.enums.TypePago;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPagoDto {

    private Double cantidad;
    private TypePago typePago;
    private LocalDate date;
    private String codigoEstudiante;

}
