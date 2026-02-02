package com.app.savings.controllers;

import com.app.savings.entities.Concepto;
import com.app.savings.repository.ConceptoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conceptos")
@CrossOrigin("*")
public class ConceptoController {

    @Autowired
    private ConceptoRepository conceptoRepository;

    @GetMapping
    public List<Concepto> listar() {
        return conceptoRepository.findAll();
    }

    @PostMapping
    public Concepto guardar(@RequestBody Concepto concepto) {
        return conceptoRepository.save(concepto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        conceptoRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public org.springframework.http.ResponseEntity<Concepto> actualizar(@PathVariable Long id,
            @RequestBody Concepto concepto) {
        return conceptoRepository.findById(id)
                .map(c -> {
                    c.setNombre(concepto.getNombre());
                    return org.springframework.http.ResponseEntity.ok(conceptoRepository.save(c));
                })
                .orElse(org.springframework.http.ResponseEntity.notFound().build());
    }
}
