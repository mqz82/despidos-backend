package com.despidos.controller;

import com.despidos.model.Funcionario;
import com.despidos.service.FuncionarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/funcionarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    @GetMapping
    public ResponseEntity<List<Funcionario>> listar() {
        return ResponseEntity.ok(funcionarioService.listarFuncionarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> obtener(@PathVariable Long id) {
        return funcionarioService.obtenerFuncionario(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Funcionario>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(funcionarioService.buscarFuncionarios(q));
    }

    @PostMapping
    public ResponseEntity<Funcionario> crear(@RequestBody Funcionario funcionario) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(funcionarioService.crearFuncionario(funcionario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> actualizar(
            @PathVariable Long id,
            @RequestBody Funcionario funcionario) {
        try {
            return ResponseEntity.ok(funcionarioService.actualizarFuncionario(id, funcionario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        funcionarioService.eliminarFuncionario(id);
        return ResponseEntity.noContent().build();
    }
}
