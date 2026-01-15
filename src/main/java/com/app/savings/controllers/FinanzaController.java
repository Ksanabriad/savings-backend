package com.app.savings.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.app.savings.entities.Finanza;
import com.app.savings.enums.MedioPago;
import com.app.savings.enums.TipoFinanza;
import com.app.savings.services.FinanzaService;

@RestController
@RequestMapping("/api/finanzas")
@CrossOrigin("*")
public class FinanzaController {

    @Autowired
    private FinanzaService finanzaService;

    @GetMapping
    public List<Finanza> listarTodasLasFinanzas() {
        return finanzaService.listarTodasLasFinanzas();
    }

    @GetMapping("/{username}")
    public List<Finanza> listarFinanzas(@PathVariable String username) {
        return finanzaService.listarFinanzasPorUsuario(username);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Finanza guardarFinanza(@RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("concepto") String concepto,
            @RequestParam("cantidad") double cantidad,
            @RequestParam("tipo") TipoFinanza tipo,
            @RequestParam("medio") MedioPago medio,
            @RequestParam("fecha") LocalDate fecha,
            @RequestParam("username") String username) throws IOException {
        return finanzaService.saveFinanza(file, concepto, cantidad, tipo, medio, fecha, username);
    }

}
