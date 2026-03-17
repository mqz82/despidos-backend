package com.despidos.controller;

import com.despidos.model.DocumentoProyecto;
import com.despidos.model.Proyecto;
import com.despidos.service.ProyectoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProyectoController {

    private final ProyectoService proyectoService;

    // ==========================================
    // PROYECTOS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Proyecto>> listar() {
        return ResponseEntity.ok(proyectoService.listarProyectos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> obtener(@PathVariable Long id) {
        return proyectoService.obtenerProyecto(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Proyecto> crear(@Valid @RequestBody Proyecto proyecto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(proyectoService.crearProyecto(proyecto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proyecto> actualizar(@PathVariable Long id, @Valid @RequestBody Proyecto proyecto) {
        try {
            return ResponseEntity.ok(proyectoService.actualizarProyecto(id, proyecto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proyectoService.eliminarProyecto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Proyecto>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(proyectoService.buscarProyectos(q));
    }

    @GetMapping("/proximos")
    public ResponseEntity<List<Proyecto>> proximasAudiencias(@RequestParam(defaultValue = "60") int dias) {
        return ResponseEntity.ok(proyectoService.getAudienciasProximas(dias));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ProyectoService.DashboardStats> dashboard() {
        return ResponseEntity.ok(proyectoService.getDashboardStats());
    }

    // ==========================================
    // DOCUMENTOS
    // ==========================================

    @PostMapping("/{id}/documentos")
    public ResponseEntity<DocumentoProyecto> agregarDocumento(
            @PathVariable Long id,
            @RequestBody DocumentoProyecto documento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proyectoService.agregarDocumento(id, documento));
    }

    @PutMapping("/documentos/{documentoId}")
    public ResponseEntity<DocumentoProyecto> actualizarDocumento(
            @PathVariable Long documentoId,
            @RequestBody DocumentoProyecto documento) {
        try {
            return ResponseEntity.ok(proyectoService.actualizarDocumento(documentoId, documento));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/documentos/{documentoId}")
    public ResponseEntity<Void> eliminarDocumento(@PathVariable Long documentoId) {
        proyectoService.eliminarDocumento(documentoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/documentos/faltantes")
    public ResponseEntity<List<DocumentoProyecto>> documentosFaltantes(@PathVariable Long id) {
        return ResponseEntity.ok(proyectoService.getDocumentosFaltantes(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        proyectoService.obtenerProyecto(id).ifPresent(p -> {
            p.setEstado(Proyecto.EstadoProyecto.valueOf(body.get("estado")));
            proyectoService.actualizarProyecto(id, p);
        });
        return ResponseEntity.ok().build();
    }
}
