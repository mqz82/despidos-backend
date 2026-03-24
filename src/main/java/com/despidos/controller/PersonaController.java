package com.despidos.controller;

import com.despidos.model.ContactoPersona;
import com.despidos.model.Persona;
import com.despidos.service.PersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/personas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PersonaController {

    private final PersonaService personaService;

    // ==========================================
    // PERSONAS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Persona>> listar() {
        return ResponseEntity.ok(personaService.listarPersonas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Persona> obtener(@PathVariable Long id) {
        return personaService.obtenerPersona(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Persona> crear(@RequestBody Persona persona) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(personaService.crearPersona(persona));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Persona> actualizar(
            @PathVariable Long id,
            @RequestBody Persona persona) {
        try {
            return ResponseEntity.ok(personaService.actualizarPersona(id, persona));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        personaService.eliminarPersona(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // CONTACTOS
    // ==========================================

    @GetMapping("/{id}/contactos")
    public ResponseEntity<List<ContactoPersona>> listarContactos(@PathVariable Long id) {
        return ResponseEntity.ok(personaService.listarContactos(id));
    }

    @PostMapping("/{id}/contactos")
    public ResponseEntity<ContactoPersona> agregarContacto(
            @PathVariable Long id,
            @RequestBody ContactoPersona contacto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(personaService.agregarContacto(id, contacto));
    }

    @DeleteMapping("/contactos/{contactoId}")
    public ResponseEntity<Void> eliminarContacto(@PathVariable Long contactoId) {
        personaService.eliminarContacto(contactoId);
        return ResponseEntity.noContent().build();
    }
}