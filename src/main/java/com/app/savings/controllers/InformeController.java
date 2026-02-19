package com.app.savings.controllers;

import com.app.savings.entities.HistorialInforme;
import com.app.savings.services.InformeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/informes")
@CrossOrigin("*")
public class InformeController {

    @Autowired
    private InformeService informeService;

    @PostMapping("/guardar")
    public ResponseEntity<HistorialInforme> guardarInforme(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nombreArchivo") String nombreArchivo) throws IOException {
        return ResponseEntity.ok(informeService.guardarInforme(file, nombreArchivo));
    }

    @PostMapping("/generar")
    public ResponseEntity<HistorialInforme> generarInforme(
            @RequestParam("username") String username,
            @RequestParam("mes") int mes,
            @RequestParam("anio") int anio) throws IOException {
        return ResponseEntity.ok(informeService.generarInformeMensual(username, mes, anio));
    }

    @GetMapping
    public List<HistorialInforme> listar(@RequestParam(value = "username", required = false) String username) {
        if (username != null && !username.isEmpty()) {
            return informeService.listarPorUsuario(username);
        }
        return informeService.listarTodos();
    }

    @GetMapping("/descargar/{id}")
    public ResponseEntity<byte[]> descargarInforme(@PathVariable Long id) {
        HistorialInforme historial = informeService.obtenerPorId(id);
        if (historial == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] content = historial.getContenido();
        if (content == null || content.length == 0) {
            content = informeService.regenerarContenido(historial);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + historial.getNombreArchivo() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(content);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInforme(@PathVariable Long id) {
        informeService.eliminarInforme(id);
        return ResponseEntity.ok().build();
    }
}
