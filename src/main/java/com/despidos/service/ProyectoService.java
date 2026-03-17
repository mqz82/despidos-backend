package com.despidos.service;

import com.despidos.model.*;
import com.despidos.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final DocumentoProyectoRepository documentoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    // ==========================================
    // CRUD PROYECTOS
    // ==========================================

    public Proyecto crearProyecto(Proyecto proyecto) {
        if (proyectoRepository.findByNumeroExpediente(proyecto.getNumeroExpediente()).isPresent()) {
            throw new RuntimeException("Ya existe un proyecto con el expediente: " + proyecto.getNumeroExpediente());
        }
        // Asignar el proyecto a cada documento antes de guardar
        if (proyecto.getDocumentos() != null){
            proyecto.getDocumentos().forEach(doc -> doc.setProyecto(proyecto));
        }

        log.info("Creando proyecto: {}", proyecto.getNumeroExpediente());
        return proyectoRepository.save(proyecto);
    }

    @Transactional(readOnly = true)
    public List<Proyecto> listarProyectos() {
        return proyectoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Proyecto> obtenerProyecto(Long id) {
        return proyectoRepository.findById(id);
    }

    public Proyecto actualizarProyecto(Long id, Proyecto proyectoActualizado) {
        Proyecto existente = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado: " + id));

        existente.setNumeroExpediente(proyectoActualizado.getNumeroExpediente());
        existente.setNombreEmpleado(proyectoActualizado.getNombreEmpleado());
        existente.setApellidoEmpleado(proyectoActualizado.getApellidoEmpleado());
        existente.setLegajo(proyectoActualizado.getLegajo());
        existente.setAreaDepartamento(proyectoActualizado.getAreaDepartamento());
        existente.setCargo(proyectoActualizado.getCargo());
        existente.setFechaIngreso(proyectoActualizado.getFechaIngreso());
        existente.setFechaDespido(proyectoActualizado.getFechaDespido());
        existente.setTipoDespido(proyectoActualizado.getTipoDespido());
        existente.setMontoIndemnizacion(proyectoActualizado.getMontoIndemnizacion());
        existente.setDniEmpleado(proyectoActualizado.getDniEmpleado());
        existente.setEmailEmpleado(proyectoActualizado.getEmailEmpleado());
        existente.setFechaAudiencia(proyectoActualizado.getFechaAudiencia());
        existente.setJuzgado(proyectoActualizado.getJuzgado());
        existente.setNumeroCausa(proyectoActualizado.getNumeroCausa());
        existente.setAbogadoEmpresa(proyectoActualizado.getAbogadoEmpresa());
        existente.setAbogadoEmpleado(proyectoActualizado.getAbogadoEmpleado());
        existente.setEstado(proyectoActualizado.getEstado());
        existente.setNotas(proyectoActualizado.getNotas());
        existente.setDiasAnticipacionAlerta(proyectoActualizado.getDiasAnticipacionAlerta());
        existente.setEmailAlerta(proyectoActualizado.getEmailAlerta());

        // Resetear alerta si se cambió la fecha de audiencia
        if (!existente.getFechaAudiencia().equals(proyectoActualizado.getFechaAudiencia())) {
            existente.setAlertaEnviada(false);
        }

        // Asignar el proyecto a cada documento
        if (proyectoActualizado.getDocumentos() != null) {
            existente.getDocumentos().clear();
            proyectoActualizado.getDocumentos().forEach(doc -> {
                doc.setProyecto(existente);
                existente.getDocumentos().add(doc);
            });
        }

        return proyectoRepository.save(existente);
    }

    public void eliminarProyecto(Long id) {
        proyectoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Proyecto> buscarProyectos(String termino) {
        return proyectoRepository.buscar(termino);
    }

    @Transactional(readOnly = true)
    public List<Proyecto> getAudienciasProximas(int dias) {
        return proyectoRepository.findAudienciasProximas(LocalDate.now(), LocalDate.now().plusDays(dias));
    }

    // ==========================================
    // DOCUMENTOS
    // ==========================================

    public DocumentoProyecto agregarDocumento(Long proyectoId, DocumentoProyecto documento) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado: " + proyectoId));
        documento.setProyecto(proyecto);
        return documentoRepository.save(documento);
    }

    public DocumentoProyecto actualizarDocumento(Long documentoId, DocumentoProyecto documentoActualizado) {
        DocumentoProyecto existente = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + documentoId));

        existente.setEstado(documentoActualizado.getEstado());
        existente.setFechaRecepcion(documentoActualizado.getFechaRecepcion());
        existente.setFechaVencimiento(documentoActualizado.getFechaVencimiento());
        existente.setNumeroReferencia(documentoActualizado.getNumeroReferencia());
        existente.setResponsable(documentoActualizado.getResponsable());
        existente.setNotas(documentoActualizado.getNotas());
        existente.setUrlArchivo(documentoActualizado.getUrlArchivo());
        existente.setNombreArchivo(documentoActualizado.getNombreArchivo());
        existente.setObligatorio(documentoActualizado.getObligatorio());

        return documentoRepository.save(existente);
    }

    public void eliminarDocumento(Long documentoId) {
        documentoRepository.deleteById(documentoId);
    }

    @Transactional(readOnly = true)
    public List<DocumentoProyecto> getDocumentosFaltantes(Long proyectoId) {
        return documentoRepository.findDocumentosObligatoriosPendientes(proyectoId);
    }

    // ==========================================
    // RESUMEN / ESTADÍSTICAS
    // ==========================================

    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats() {
        long total = proyectoRepository.count();
        long activos = proyectoRepository.findByEstado(Proyecto.EstadoProyecto.ACTIVO).size();
        long enJuicio = proyectoRepository.findByEstado(Proyecto.EstadoProyecto.EN_JUICIO).size();
        long resueltos = proyectoRepository.findByEstado(Proyecto.EstadoProyecto.RESUELTO).size();
        long conDocFaltantes = proyectoRepository.findProyectosConDocumentosFaltantes().size();
        List<Proyecto> audienciasHoy = proyectoRepository.findAudienciasProximas(
                LocalDate.now(), LocalDate.now().plusDays(7));

        return new DashboardStats(total, activos, enJuicio, resueltos, conDocFaltantes, audienciasHoy.size());
    }

    public record DashboardStats(
            long totalProyectos,
            long activos,
            long enJuicio,
            long resueltos,
            long conDocumentosFaltantes,
            long audienciasProximas7Dias
    ) {}
}
