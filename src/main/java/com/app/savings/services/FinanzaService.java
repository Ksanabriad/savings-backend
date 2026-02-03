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

import com.app.savings.entities.Concepto;
import com.app.savings.entities.Finanza;
import com.app.savings.entities.MedioPago;
import com.app.savings.entities.TipoFinanza;
import com.app.savings.entities.Usuario;

import com.app.savings.repository.*;

@Service
public class FinanzaService {

    @Autowired
    private FinanzaRepository finanzaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoFinanzaRepository tipoFinanzaRepository;

    @Autowired
    private MedioPagoRepository medioPagoRepository;

    @Autowired
    private ConceptoRepository conceptoRepository;

    public Finanza saveFinanza(MultipartFile file, String conceptoNombre, double cantidad,
            String tipoNombre, String medioNombre, LocalDate date,
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
        TipoFinanza tipo = tipoFinanzaRepository.findByNombre(tipoNombre).orElse(null);
        MedioPago medio = medioPagoRepository.findByNombre(medioNombre).orElse(null);
        Concepto concepto = conceptoRepository.findByNombre(conceptoNombre).orElse(null);

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

    public Finanza getFinanzaPorId(Long id) {
        return finanzaRepository.findById(id).orElse(null);
    }

    public Finanza actualizarFinanza(Long id, MultipartFile file, String conceptoNombre, double cantidad,
            String tipoNombre, String medioNombre, LocalDate date, String username) throws IOException {

        Finanza finanzaExistente = finanzaRepository.findById(id).orElse(null);
        if (finanzaExistente == null) {
            return null;
        }

        String filePathString = finanzaExistente.getFile();

        // Si se sube un nuevo archivo, reemplazamos el anterior
        if (file != null && !file.isEmpty()) {
            Path folderPath = Paths.get(System.getProperty("user.home"), "enset-data", "finanzas");
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            String fileName = UUID.randomUUID().toString() + ".pdf";
            Path filePath = folderPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            filePathString = filePath.toUri().toString();
        }

        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        TipoFinanza tipo = tipoFinanzaRepository.findByNombre(tipoNombre).orElse(null);
        MedioPago medio = medioPagoRepository.findByNombre(medioNombre).orElse(null);
        Concepto concepto = conceptoRepository.findByNombre(conceptoNombre).orElse(null);

        finanzaExistente.setConcepto(concepto);
        finanzaExistente.setCantidad(cantidad);
        finanzaExistente.setTipo(tipo);
        finanzaExistente.setMedio(medio);
        finanzaExistente.setFecha(date);
        finanzaExistente.setUsuario(usuario);
        finanzaExistente.setFile(filePathString);

        return finanzaRepository.save(finanzaExistente);
    }

    public void eliminarFinanza(Long id) {
        finanzaRepository.deleteById(id);
    }

    public Double getBalance(String username) {
        Double ingresos = finanzaRepository.sumByUsuarioAndTipo(username, "INGRESO");
        Double egresos = finanzaRepository.sumByUsuarioAndTipo(username, "EGRESO");
        if (ingresos == null)
            ingresos = 0.0;
        if (egresos == null)
            egresos = 0.0;
        return ingresos - egresos;
    }

    public List<com.app.savings.dtos.MonthlyExpenseDto> getLast3MonthsExpenses(String username) {
        LocalDate now = LocalDate.now();
        // Start of current month (exclusive boundary for query)
        LocalDate endDate = now.withDayOfMonth(1);
        // 3 months prior
        LocalDate startDate = endDate.minusMonths(3);

        return finanzaRepository.findMonthlyExpenses(username, startDate, endDate);
    }

    public List<com.app.savings.dtos.MonthlyExpenseDto> getLast3MonthsIncome(String username) {
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.withDayOfMonth(1);
        LocalDate startDate = endDate.minusMonths(3);

        return finanzaRepository.findMonthlyIncome(username, startDate, endDate);
    }

    public Double getCurrentMonthIncome(String username) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        Double income = finanzaRepository.sumByUsuarioAndTipoAndDateRange(username, "INGRESO", startOfMonth,
                now.plusDays(1));
        return income != null ? income : 0.0;
    }

    public Double getCurrentMonthExpenses(String username) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        Double expenses = finanzaRepository.sumByUsuarioAndTipoAndDateRange(username, "EGRESO", startOfMonth,
                now.plusDays(1));
        return expenses != null ? expenses : 0.0;
    }

    public List<java.util.Map<String, Object>> getCategoryDistribution(String username) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        return finanzaRepository.findCategoryDistribution(username, startOfMonth, now.plusDays(1));
    }

    public List<Finanza> getRecentTransactions(String username, int limit) {
        return finanzaRepository.findTopByUsuarioUsernameOrderByFechaDesc(username, limit);
    }
}
