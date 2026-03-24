package com.despidos.controller;

import com.despidos.model.Abogado;
import com.despidos.service.AbogadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/abogados")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AbogadoController {

    private final AbogadoService abogadoService;

    @GetMapping
    public ResponseEntity<List<Abogado>> listar() {
        return ResponseEntity.ok(abogadoService.listarAbogados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Abogado> obtener(@PathVariable Long id) {
        return abogadoService.obtenerAbogado(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Abogado>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(abogadoService.buscarAbogados(q));
    }

    @PostMapping
    public ResponseEntity<Abogado> crear(@RequestBody Abogado abogado) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(abogadoService.crearAbogado(abogado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Abogado> actualizar(
            @PathVariable Long id,
            @RequestBody Abogado abogado) {
        try {
            return ResponseEntity.ok(abogadoService.actualizarAbogado(id, abogado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        abogadoService.eliminarAbogado(id);
        return ResponseEntity.noContent().build();
    }
}