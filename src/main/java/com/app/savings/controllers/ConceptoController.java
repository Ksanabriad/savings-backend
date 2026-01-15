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
}
