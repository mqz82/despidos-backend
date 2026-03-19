package com.despidos.controller;

import com.despidos.model.DocumentoProyecto;
import com.despidos.repository.DocumentoProyectoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/archivos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class ArchivoController {

    @Value("${archivos.upload-dir:uploads}")
    private String uploadDir;


    private final DocumentoProyectoRepository documentoRepo;


    // SUBIR archivo
    @PostMapping("/subir/{documentoId}")
    public ResponseEntity<DocumentoProyecto> subirArchivo(
            @PathVariable Long documentoId,
            @RequestParam("archivo") MultipartFile archivo) throws IOException {

        DocumentoProyecto doc = documentoRepo.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + documentoId));

        String tipo = archivo.getContentType();
        // Validar que sea PDF o Word
        if (tipo == null || (!tipo.equals("application/pdf") &&
                !tipo.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            return ResponseEntity.badRequest().build();
        }

        doc.setNombreArchivo(archivo.getOriginalFilename());
        doc.setArchivoContenido(archivo.getBytes());
        doc.setArchivoTipo(tipo);
        doc.setEstado(DocumentoProyecto.EstadoDocumento.RECIBIDO);
        documentoRepo.save(doc);

        log.info("Archivo guardado en BD: {} para documento: {}", archivo.getOriginalFilename(), documentoId);

        // Devolver sin el contenido binario para no sobrecargar la respuesta
        doc.setArchivoContenido(null);
        return ResponseEntity.ok(doc);
    }

    // DESCARGAR archivo
    @GetMapping("/descargar/{documentoId}")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Long documentoId) {

        DocumentoProyecto doc = documentoRepo.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + documentoId));

        if (doc.getArchivoContenido() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getArchivoTipo()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getNombreArchivo() + "\"")
                .body(doc.getArchivoContenido());
    }

    // ELIMINAR archivo
    @DeleteMapping("/eliminar/{documentoId}")
    public ResponseEntity<Void> eliminarArchivo(@PathVariable Long documentoId) {

        DocumentoProyecto doc = documentoRepo.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + documentoId));

        doc.setArchivoContenido(null);
        doc.setNombreArchivo(null);
        doc.setArchivoTipo(null);
        documentoRepo.save(doc);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expediente/{proyectoId}")
    public ResponseEntity<List<Map<String, Object>>> listarArchivos(@PathVariable Long proyectoId) {

        List<DocumentoProyecto> docs = documentoRepo.findByProyectoId(proyectoId);

        List<Map<String, Object>> resultado = docs.stream()
                .filter(d -> d.getNombreArchivo() != null)
                .map(d -> {
                    Map<String, Object> item = new java.util.HashMap<>();
                    item.put("id", d.getId());
                    item.put("nombreArchivo", d.getNombreArchivo());
                    item.put("archivoTipo", d.getArchivoTipo() != null ? d.getArchivoTipo() : "");
                    item.put("tipoDocumento", d.getTipoDocumento().getNombre());
                    item.put("estado", d.getEstado().toString());
                    return item;
                })
                .toList();

        return ResponseEntity.ok(resultado);
    }


    @PostMapping("/subir-fisico/{documentoId}")
    public ResponseEntity<DocumentoProyecto> subirDocumentoFisico(@PathVariable Long documentoId, @RequestParam("archivo") MultipartFile archivo) throws IOException {

        DocumentoProyecto doc = documentoRepo.findById(documentoId).orElseThrow(() -> new RuntimeException("Documento no encontrado;"+ documentoId ));

        // Obtener nombre del expediente para crear carpeta
        String nombreCarpeta = doc.getProyecto().getNombreExpediente()
                .replaceAll("[^a-zA-Z0-9\\-]", "-").toLowerCase();

        // Crear carpeta con nombre del expediente
        Path carpeta = Paths.get(uploadDir, nombreCarpeta);
        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }

        // Guardar archivo en disco
        String nombreArchivo = archivo.getOriginalFilename();
        Path rutaArchivo = carpeta.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        log.info("ruta archivo : "+ rutaArchivo );

        // Actualizar documento en BD
        doc.setNombreArchivo(nombreArchivo);
        doc.setRutaFisica(rutaArchivo.toString());
        doc.setGuardadoFisico(true);
        doc.setEstado(DocumentoProyecto.EstadoDocumento.RECIBIDO);
        documentoRepo.save(doc);

        log.info("Archivo guardado físicamente: {} en carpeta: {}", nombreArchivo, nombreCarpeta);

        return ResponseEntity.ok(doc);

    }

}