package com.despidos.service;

import com.despidos.model.Funcionario;
import com.despidos.model.Persona;
import com.despidos.repository.FuncionarioRepository;
import com.despidos.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepo;
    private final PersonaRepository personaRepo;

    // ==========================================
    // CRUD FUNCIONARIO
    // ==========================================

    public Funcionario crearFuncionario(Funcionario funcionario) {
        // Primero guardar la persona
        Persona persona = personaRepo.save(funcionario.getPersona());
        funcionario.setPersona(persona);
        log.info("Creando funcionario: {} {}", persona.getNombres(), persona.getAppPaterno());
        return funcionarioRepo.save(funcionario);
    }

    @Transactional(readOnly = true)
    public List<Funcionario> listarFuncionarios() {
        return funcionarioRepo.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Funcionario> obtenerFuncionario(Long id) {
        return funcionarioRepo.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Funcionario> buscarFuncionarios(String termino) {
        return funcionarioRepo.buscar(termino);
    }

    public Funcionario actualizarFuncionario(Long id, Funcionario funcionarioActualizado) {
        Funcionario existente = funcionarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionario no encontrado: " + id));

        // Actualizar datos de persona
        Persona persona = existente.getPersona();
        persona.setNombres(funcionarioActualizado.getPersona().getNombres());
        persona.setAppPaterno(funcionarioActualizado.getPersona().getAppPaterno());
        persona.setAppMaterno(funcionarioActualizado.getPersona().getAppMaterno());
        persona.setRut(funcionarioActualizado.getPersona().getRut());
        persona.setGenero(funcionarioActualizado.getPersona().getGenero());
        personaRepo.save(persona);

        // Actualizar datos propios del funcionario
        existente.setCargo(funcionarioActualizado.getCargo());
        existente.setDepartamento(funcionarioActualizado.getDepartamento());
        existente.setInstitucion(funcionarioActualizado.getInstitucion());

        return funcionarioRepo.save(existente);
    }

    public void eliminarFuncionario(Long id) {
        Funcionario funcionario = funcionarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionario no encontrado: " + id));
        funcionario.setActivo(false);
        funcionarioRepo.save(funcionario);
    }
}