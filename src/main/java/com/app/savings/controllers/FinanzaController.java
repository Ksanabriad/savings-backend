package com.app.savings.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/detail/{id}")
    public ResponseEntity<Finanza> getFinanzaPorId(@PathVariable Long id) {
        Finanza finanza = finanzaService.getFinanzaPorId(id);
        if (finanza != null) {
            return ResponseEntity.ok(finanza);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Finanza> actualizarFinanza(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("concepto") String concepto,
            @RequestParam("cantidad") double cantidad,
            @RequestParam("tipo") String tipo,
            @RequestParam("medio") String medio,
            @RequestParam("fecha") LocalDate fecha,
            @RequestParam("username") String username) throws IOException {
        Finanza actualizada = finanzaService.actualizarFinanza(id, file, concepto, cantidad, tipo, medio, fecha,
                username);
        if (actualizada != null) {
            return ResponseEntity.ok(actualizada);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFinanza(@PathVariable Long id) {
        finanzaService.eliminarFinanza(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/balance")
    public ResponseEntity<Double> getBalance(@PathVariable String username) {
        return ResponseEntity.ok(finanzaService.getBalance(username));
    }

    @GetMapping("/{username}/expenses-last-3-months")
    public ResponseEntity<List<com.app.savings.dtos.MonthlyExpenseDto>> getLast3MonthsExpenses(
            @PathVariable String username) {
        return ResponseEntity.ok(finanzaService.getLast3MonthsExpenses(username));
    }

    @GetMapping("/{username}/income-last-3-months")
    public ResponseEntity<List<com.app.savings.dtos.MonthlyExpenseDto>> getLast3MonthsIncome(
            @PathVariable String username) {
        return ResponseEntity.ok(finanzaService.getLast3MonthsIncome(username));
    }

    @GetMapping("/{username}/current-month-income")
    public ResponseEntity<Double> getCurrentMonthIncome(@PathVariable String username) {
        return ResponseEntity.ok(finanzaService.getCurrentMonthIncome(username));
    }

    @GetMapping("/{username}/current-month-expenses")
    public ResponseEntity<Double> getCurrentMonthExpenses(@PathVariable String username) {
        return ResponseEntity.ok(finanzaService.getCurrentMonthExpenses(username));
    }

    @GetMapping("/{username}/category-distribution")
    public ResponseEntity<List<Map<String, Object>>> getCategoryDistribution(@PathVariable String username) {
        return ResponseEntity.ok(finanzaService.getCategoryDistribution(username));
    }

    @GetMapping("/{username}/recent")
    public ResponseEntity<List<Finanza>> getRecentTransactions(@PathVariable String username) {
        return ResponseEntity.ok(finanzaService.getRecentTransactions(username, 3));
    }
}
