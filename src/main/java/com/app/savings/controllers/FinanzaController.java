package com.app.savings.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.app.savings.entities.*;
import com.app.savings.repository.*;
import com.app.savings.services.FinanzaService;

@RestController
@RequestMapping("/api/finanzas")
@CrossOrigin("*")
public class FinanzaController {

    @Autowired
    private FinanzaService finanzaService;

    @Autowired
    private TipoFinanzaRepository tipoFinanzaRepository;

    @Autowired
    private MedioPagoRepository medioPagoRepository;

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
            @RequestParam("tipo") String tipo,
            @RequestParam("medio") String medio,
            @RequestParam("fecha") LocalDate fecha,
            @RequestParam("username") String username) throws IOException {
        return finanzaService.saveFinanza(file, concepto, cantidad, tipo, medio, fecha, username);
    }

    @GetMapping("/tipos")
    public List<TipoFinanza> listarTipos() {
        return tipoFinanzaRepository.findAll();
    }

    @GetMapping("/medios")
    public List<MedioPago> listarMedios() {
        return medioPagoRepository.findAll();
    }

    @GetMapping("/archivo/{id}")
    public ResponseEntity<byte[]> getArchivo(@PathVariable Long id) throws IOException {
        byte[] file = finanzaService.getArchivoPorId(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}
