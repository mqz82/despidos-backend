package com.despidos.controller;

import com.despidos.model.TipoDocumento;
import com.despidos.repository.TipoDocumentoRepository;
import com.despidos.service.AlertaService;
import com.despidos.service.ConfiguracionService;
import com.despidos.model.ConfiguracionSistema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ============================================================
// TIPOS DE DOCUMENTO
// ============================================================
@RestController
@RequestMapping("/api/tipos-documento")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
class TipoDocumentoController {

    private final TipoDocumentoRepository repo;

    @GetMapping
    public List<TipoDocumento> listar() {
        return repo.findByActivoTrueOrderByOrdenAsc();
    }

    @GetMapping("/todos")
    public List<TipoDocumento> listarTodos() {
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<TipoDocumento> crear(@RequestBody TipoDocumento tipo) {
        if (repo.existsByNombre(tipo.getNombre())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(tipo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoDocumento> actualizar(@PathVariable Long id, @RequestBody TipoDocumento tipo) {
        return repo.findById(id).map(existing -> {
            existing.setNombre(tipo.getNombre());
            existing.setDescripcion(tipo.getDescripcion());
            existing.setObligatorio(tipo.getObligatorio());
            existing.setActivo(tipo.getActivo());
            existing.setOrden(tipo.getOrden());
            existing.setCategoria(tipo.getCategoria());
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        repo.findById(id).ifPresent(t -> {
            t.setActivo(false);
            repo.save(t);
        });
        return ResponseEntity.noContent().build();
    }
}

// ============================================================
// CONFIGURACIÓN DEL SISTEMA
// ============================================================
@RestController
@RequestMapping("/api/configuracion")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
class ConfiguracionController {

    private final ConfiguracionService configuracionService;
    private final AlertaService alertaService;

    @GetMapping
    public List<ConfiguracionSistema> listar() {
        return configuracionService.listarConfiguraciones();
    }

    @PostMapping
    public ResponseEntity<ConfiguracionSistema> guardar(@RequestBody ConfiguracionSistema config) {
        return ResponseEntity.ok(configuracionService.guardar(config));
    }

    @PutMapping("/{clave}")
    public ResponseEntity<ConfiguracionSistema> actualizar(
            @PathVariable String clave,
            @RequestBody java.util.Map<String, String> body) {
        try {
            return ResponseEntity.ok(configuracionService.actualizarValor(clave, body.get("valor")));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/alertas/ejecutar")
    public ResponseEntity<AlertaService.AlertaResult> ejecutarAlertas() {
        return ResponseEntity.ok(alertaService.verificarAlertasManual());
    }
}
