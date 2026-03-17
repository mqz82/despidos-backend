package com.despidos.service;

import com.despidos.model.DocumentoProyecto;
import com.despidos.model.Proyecto;
import com.despidos.repository.DocumentoProyectoRepository;
import com.despidos.repository.ProyectoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertaService {

    private final ProyectoRepository proyectoRepository;
    private final DocumentoProyectoRepository documentoRepository;
    private final ConfiguracionService configuracionService;
    private final JavaMailSender mailSender;

    @Value("${alertas.email-destino:legal@empresa.com}")
    private String emailDestinoDefault;

    /**
     * Job programado - se ejecuta según el cron configurado en properties
     * Default: lunes a viernes a las 8:00 AM
     */
    @Scheduled(cron = "${alertas.cron:0 0 8 * * MON-FRI}")
    public void verificarAlertas() {
        log.info("=== Iniciando verificación de alertas: {} ===", LocalDateTime.now());

        int diasAnticipacion = configuracionService.getDiasAnticipacion();
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(diasAnticipacion);

        List<Proyecto> proyectosParaAlertar = proyectoRepository.findProyectosParaAlertar(hoy, fechaLimite);

        log.info("Proyectos que requieren alerta: {}", proyectosParaAlertar.size());

        for (Proyecto proyecto : proyectosParaAlertar) {
            procesarAlerta(proyecto);
        }
    }

    /**
     * Procesamiento manual de alertas (puede ser disparado desde API)
     */
    public AlertaResult verificarAlertasManual() {
        int diasAnticipacion = configuracionService.getDiasAnticipacion();
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(diasAnticipacion);

        List<Proyecto> proyectosParaAlertar = proyectoRepository.findProyectosParaAlertar(hoy, fechaLimite);
        int alertasEnviadas = 0;

        for (Proyecto proyecto : proyectosParaAlertar) {
            if (procesarAlerta(proyecto)) {
                alertasEnviadas++;
            }
        }

        return new AlertaResult(proyectosParaAlertar.size(), alertasEnviadas);
    }

    private boolean procesarAlerta(Proyecto proyecto) {
        List<DocumentoProyecto> docsFaltantes = documentoRepository
                .findDocumentosObligatoriosPendientes(proyecto.getId());

        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), proyecto.getFechaAudiencia());

        // Solo alertar si hay documentos faltantes O si es la primera alerta
        boolean tieneDocsFaltantes = !docsFaltantes.isEmpty();

        String emailDestino = proyecto.getEmailAlerta() != null
                ? proyecto.getEmailAlerta()
                : configuracionService.getEmailDestino();

        try {
            enviarEmailAlerta(proyecto, docsFaltantes, diasRestantes, emailDestino);

            // Marcar como alertado
            proyecto.setAlertaEnviada(true);
            proyecto.setUltimaAlertaEnviada(LocalDateTime.now());
            proyectoRepository.save(proyecto);

            log.info("Alerta enviada para proyecto: {} - Días restantes: {} - Docs faltantes: {}",
                    proyecto.getNombreExpediente(), diasRestantes, docsFaltantes.size());
            return true;

        } catch (Exception e) {
            log.error("Error al enviar alerta para proyecto {}: {}", proyecto.getNombreExpediente(), e.getMessage());
            return false;
        }
    }

    private void enviarEmailAlerta(Proyecto proyecto, List<DocumentoProyecto> docsFaltantes,
                                    long diasRestantes, String emailDestino) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(emailDestino);
        mensaje.setSubject(construirAsunto(proyecto, diasRestantes, docsFaltantes.isEmpty()));
        mensaje.setText(construirCuerpo(proyecto, docsFaltantes, diasRestantes));

        mailSender.send(mensaje);
    }

    private String construirAsunto(Proyecto proyecto, long diasRestantes, boolean sinDocsFaltantes) {
        String urgencia = diasRestantes <= 7 ? "🚨 URGENTE - " : diasRestantes <= 15 ? "⚠️ " : "";
        String docStr = sinDocsFaltantes ? "" : " [DOCUMENTOS FALTANTES]";
        return String.format("%sAudiencia en %d días - %s %s%s",
                urgencia, diasRestantes,
                proyecto.getNombreEmpleado(), proyecto.getApellidoEmpleado(),
                docStr);
    }

    private String construirCuerpo(Proyecto proyecto, List<DocumentoProyecto> docsFaltantes, long diasRestantes) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALERTA DEL SISTEMA DE GESTIÓN DE DESPIDOS\n");
        sb.append("==========================================\n\n");

        sb.append(String.format("EXPEDIENTE: %s\n", proyecto.getNombreExpediente()));
        sb.append(String.format("EMPLEADO: %s %s\n", proyecto.getNombreEmpleado(), proyecto.getApellidoEmpleado()));
        sb.append(String.format("FECHA DE AUDIENCIA: %s\n", proyecto.getFechaAudiencia()));
        sb.append(String.format("DÍAS RESTANTES: %d días\n", diasRestantes));

        if (proyecto.getJuzgado() != null) {
            sb.append(String.format("JUZGADO: %s\n", proyecto.getJuzgado()));
        }
        if (proyecto.getNumeroCausa() != null) {
            sb.append(String.format("NÚMERO DE CAUSA: %s\n", proyecto.getNumeroCausa()));
        }

        sb.append("\n");

        if (!docsFaltantes.isEmpty()) {
            sb.append("⚠️  DOCUMENTOS OBLIGATORIOS PENDIENTES:\n");
            sb.append("----------------------------------------\n");
            for (DocumentoProyecto doc : docsFaltantes) {
                sb.append(String.format("  • %s\n", doc.getTipoDocumento().getNombre()));
            }
            sb.append("\nACCIÓN REQUERIDA: Por favor, gestionar los documentos faltantes antes de la audiencia.\n");
        } else {
            sb.append("✅ Todos los documentos obligatorios están en orden.\n");
        }

        sb.append("\n--\nSistema de Gestión de Casos de Despido");
        return sb.toString();
    }

    public record AlertaResult(int proyectosEvaluados, int alertasEnviadas) {}
}
