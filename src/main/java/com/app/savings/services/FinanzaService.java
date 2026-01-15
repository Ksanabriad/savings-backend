package com.app.savings.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.savings.entities.Finanza;
import com.app.savings.entities.Usuario;
import com.app.savings.enums.MedioPago;
import com.app.savings.enums.TipoFinanza;
import com.app.savings.repository.FinanzaRepository;
import com.app.savings.repository.UsuarioRepository;

@Service
public class FinanzaService {

    @Autowired
    private FinanzaRepository finanzaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Finanza saveFinanza(MultipartFile file, String concepto, double cantidad,
            TipoFinanza tipo, MedioPago medio, LocalDate date,
            String username) throws IOException {

        String fileName = "";
        String filePathString = "";

        if (file != null && !file.isEmpty()) {
            Path folderPath = Paths.get(System.getProperty("user.home"), "enset-data", "finanzas");
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            fileName = UUID.randomUUID().toString() + ".pdf";
            Path filePath = folderPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            filePathString = filePath.toUri().toString();
        }

        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

        Finanza finanza = Finanza.builder()
                .concepto(concepto)
                .cantidad(cantidad)
                .tipo(tipo)
                .medio(medio)
                .fecha(date)
                .usuario(usuario)
                .file(filePathString)
                .build();

        return finanzaRepository.save(finanza);
    }

    public List<Finanza> listarFinanzasPorUsuario(String username) {
        return finanzaRepository.findByUsuarioUsername(username);
    }

    public List<Finanza> listarTodasLasFinanzas() {
        return finanzaRepository.findAll();
    }

    public byte[] getArchivoPorId(Long id) throws IOException {
        Finanza finanza = finanzaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finanza no encontrada"));
        if (finanza.getFile() == null || finanza.getFile().isEmpty()) {
            return null;
        }
        Path filePath = Paths.get(finanza.getFile().replace("file:/", ""));
        return Files.readAllBytes(filePath);
    }

    public void eliminarFinanza(Long id) {
        finanzaRepository.deleteById(id);
    }
}
