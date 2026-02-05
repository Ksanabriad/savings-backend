package com.app.savings.services;

import com.app.savings.entities.Finanza;
import com.app.savings.entities.HistorialInforme;
import com.app.savings.entities.Usuario;
import com.app.savings.repository.FinanzaRepository;
import com.app.savings.repository.HistorialInformeRepository;
import com.app.savings.repository.UsuarioRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class InformeService {

    @Autowired
    private HistorialInformeRepository historialInformeRepository;

    @Autowired
    private FinanzaRepository finanzaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public HistorialInforme guardarInforme(MultipartFile file, String nombreArchivo) throws IOException {
        HistorialInforme historial = HistorialInforme.builder()
                .nombreArchivo(nombreArchivo)
                .fechaGeneracion(LocalDate.now())
                .contenido(file.getBytes())
                .build();
        return historialInformeRepository.save(historial);
    }

    public HistorialInforme generarInformeMensual(String username, int mes, int anio) throws IOException {
        Usuario usuario = usuarioRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Finanza> finanzas = finanzaRepository.findByUsuarioAndMesAndAnio(usuario, mes, anio);

        LocalDate lastDayOfMonth = java.time.YearMonth.of(anio, mes).atEndOfMonth();
        if (LocalDate.now().compareTo(lastDayOfMonth) <= 0) {
            throw new RuntimeException("El informe solo se puede generar al finalizar el mes.");
        }

        String mesNombre = java.time.Month.of(mes).getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES"));
        // Capitalize first letter
        mesNombre = mesNombre.substring(0, 1).toUpperCase() + mesNombre.substring(1).toLowerCase();

        String nombreArchivo = username + "_" + mesNombre + "_" + anio + ".pdf";
        LocalDate fechaGeneracion = LocalDate.now();

        byte[] pdfContent = generarContenidoPDF(usuario, mesNombre, anio, finanzas, fechaGeneracion);

        HistorialInforme historial = HistorialInforme.builder()
                .nombreArchivo(nombreArchivo)
                .fechaGeneracion(fechaGeneracion)
                .usuario(usuario)
                .finanzas(finanzas)
                .contenido(pdfContent)
                .build();

        return historialInformeRepository.save(historial);
    }

    public byte[] generarContenidoPDF(Usuario usuario, String mesNombre, int anio, List<Finanza> finanzas,
            LocalDate fechaGen) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // Estilos
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(44, 62, 80));
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(52, 73, 94));
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font footFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY);

        // Título
        Paragraph title = new Paragraph("ESTADO DE CUENTA MENSUAL - EASYSAVE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Info Usuario y Periodo
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(10);

        infoTable.addCell(getNoBorderCell("Usuario: " + usuario.getUsername(), subtitleFont));
        infoTable.addCell(getNoBorderCell("Periodo: " + mesNombre.toUpperCase() + " " + anio, subtitleFont));
        infoTable.addCell(getNoBorderCell("Fecha Emisión: " + fechaGen.toString(), cellFont));
        infoTable.addCell(getNoBorderCell("Email: " + usuario.getEmail(), cellFont));

        document.add(infoTable);
        document.add(new Paragraph(" "));

        // Tabla de movimientos
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 2, 3.5f, 1.5f, 2f, 2 });
        table.setSpacingBefore(10);

        // Cabecera
        String[] headers = { "Fecha", "Concepto", "Tipo", "Medio", "Cantidad" };
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
            cell.setBackgroundColor(new Color(41, 128, 185));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        double totalIngresos = 0;
        double totalEgresos = 0;

        for (Finanza f : finanzas) {
            table.addCell(createStyledCell(f.getFecha().toString(), cellFont, Element.ALIGN_CENTER));
            table.addCell(createStyledCell(f.getConcepto() != null ? f.getConcepto().getNombre() : "N/A", cellFont,
                    Element.ALIGN_LEFT));
            table.addCell(createStyledCell(f.getTipo().getNombre(), cellFont, Element.ALIGN_CENTER));
            table.addCell(createStyledCell(f.getMedio().getNombre(), cellFont, Element.ALIGN_CENTER));

            String cantidadStr = String.format("%.2f €", f.getCantidad());
            PdfPCell cantCell = createStyledCell(cantidadStr, cellFont, Element.ALIGN_RIGHT);
            if (f.getTipo().getNombre().equalsIgnoreCase("INGRESO")) {
                totalIngresos += f.getCantidad();
                cantCell.setBackgroundColor(new Color(232, 245, 233));
            } else {
                totalEgresos += f.getCantidad();
                cantCell.setBackgroundColor(new Color(255, 235, 238));
            }
            table.addCell(cantCell);
        }

        document.add(table);
        document.add(new Paragraph(" "));

        // Resumen
        PdfPTable resumenTable = new PdfPTable(2);
        resumenTable.setWidthPercentage(40);
        resumenTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        resumenTable.addCell(getSummaryCell("Total Ingresos:", cellFont));
        resumenTable.addCell(getSummaryValueCell(String.format("%.2f €", totalIngresos), cellFont));

        resumenTable.addCell(getSummaryCell("Total Egresos:", cellFont));
        resumenTable.addCell(getSummaryValueCell(String.format("%.2f €", totalEgresos), cellFont));

        double balance = totalIngresos - totalEgresos;
        Font balFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11,
                balance >= 0 ? new Color(39, 174, 96) : new Color(192, 57, 43));
        resumenTable.addCell(getSummaryCell("BALANCE NETO:", balFont));
        resumenTable.addCell(getSummaryValueCell(String.format("%.2f €", balance), balFont));

        document.add(resumenTable);

        document.add(new Paragraph("\n\n"));
        Paragraph footer = new Paragraph("Este documento es un extracto oficial generado por la plataforma EasySave.",
                footFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    private PdfPCell getNoBorderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);
        return cell;
    }

    private PdfPCell createStyledCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell getSummaryCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(Color.LIGHT_GRAY);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell getSummaryValueCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(Color.LIGHT_GRAY);
        cell.setPadding(5);
        return cell;
    }

    public List<HistorialInforme> listarTodos() {
        return historialInformeRepository.findAll();
    }

    public List<HistorialInforme> listarPorUsuario(String username) {
        Usuario usuario = usuarioRepository.findById(username).orElse(null);
        if (usuario == null)
            return List.of();
        return historialInformeRepository.findByUsuario(usuario);
    }

    public HistorialInforme obtenerPorId(Long id) {
        return historialInformeRepository.findById(id).orElse(null);
    }

    public byte[] regenerarContenido(HistorialInforme historial) {
        if (historial.getUsuario() == null || historial.getFinanzas() == null) {
            return new byte[0];
        }

        int mes = historial.getFechaGeneracion().getMonthValue();
        int anio = historial.getFechaGeneracion().getYear();
        String mesNombre = java.time.Month.of(mes).getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES"));

        byte[] content = generarContenidoPDF(historial.getUsuario(), mesNombre, anio, historial.getFinanzas(),
                historial.getFechaGeneracion());

        // Opcional: guardar el contenido generado para futuras descargas
        historial.setContenido(content);
        historialInformeRepository.save(historial);

        return content;
    }

    public void eliminarInforme(Long id) {
        historialInformeRepository.deleteById(id);
    }
}
