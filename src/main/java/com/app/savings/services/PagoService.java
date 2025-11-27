package com.app.savings.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.savings.entities.Estudiante;
import com.app.savings.entities.Pago;
import com.app.savings.enums.PagoStatus;
import com.app.savings.enums.TypePago;
import com.app.savings.repository.EstudianteRepository;
import com.app.savings.repository.PagoRepository;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    public Pago savePago(MultipartFile file, double cantidad, TypePago type, LocalDate date, String codigoEstudiante) throws IOException {
        Path folderPath = Paths.get(System.getProperty("user.home"), "enset-data", "pagos");
        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        }

        String fileName = UUID.randomUUID().toString();
        Path filePath = Paths.get(System.getProperty("user.home"), "enset-data", "pagos", fileName + ".pdf");
        Files.copy(file.getInputStream(), filePath);

        Estudiante estudiante = estudianteRepository.findByCodigo(codigoEstudiante);
        Pago pago = Pago.builder()
                .type(type)
                .status(PagoStatus.CREADO)
                .fecha(date)
                .estudiante(estudiante)
                .cantidad(cantidad)
                .file(filePath.toUri().toString())
                .build();
        return pagoRepository.save(pago);
    }

    public byte[] getArchivoPorId(Long pagoId) throws IOException {
        Pago pago = pagoRepository.findById(pagoId).get();
        Path filePath = Paths.get(pago.getFile().replace("file:/", ""));
        return Files.readAllBytes(filePath);
    }

    public Pago actualizarPagoPorStatus( PagoStatus newStatus,Long pagoId) {
        Pago pago = pagoRepository.findById(pagoId).get();
        pago.setStatus(newStatus);
        return pagoRepository.save(pago);
    }

}
