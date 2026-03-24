package com.despidos.service;

import com.despidos.model.Abogado;
import com.despidos.model.Persona;
import com.despidos.repository.AbogadoRepository;
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
public class AbogadoService {

    private final AbogadoRepository abogadoRepo;
    private final PersonaRepository personaRepo;

    // ==========================================
    // CRUD ABOGADO
    // ==========================================

    public Abogado crearAbogado(Abogado abogado) {
        // Primero guardar la persona
        Persona persona = personaRepo.save(abogado.getPersona());
        abogado.setPersona(persona);
        log.info("Creando abogado: {} {}", persona.getNombres(), persona.getAppPaterno());
        return abogadoRepo.save(abogado);
    }

    @Transactional(readOnly = true)
    public List<Abogado> listarAbogados() {
        return abogadoRepo.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Abogado> obtenerAbogado(Long id) {
        return abogadoRepo.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Abogado> buscarAbogados(String termino) {
        return abogadoRepo.buscar(termino);
    }

    public Abogado actualizarAbogado(Long id, Abogado abogadoActualizado) {
        Abogado existente = abogadoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Abogado no encontrado: " + id));

        // Actualizar datos de persona
        Persona persona = existente.getPersona();
        persona.setNombres(abogadoActualizado.getPersona().getNombres());
        persona.setAppPaterno(abogadoActualizado.getPersona().getAppPaterno());
        persona.setAppMaterno(abogadoActualizado.getPersona().getAppMaterno());
        persona.setRut(abogadoActualizado.getPersona().getRut());
        persona.setGenero(abogadoActualizado.getPersona().getGenero());
        personaRepo.save(persona);

        // Actualizar datos propios del abogado
        existente.setNumeroColegiatura(abogadoActualizado.getNumeroColegiatura());
        existente.setEspecialidad(abogadoActualizado.getEspecialidad());
        existente.setEstudioJuridico(abogadoActualizado.getEstudioJuridico());

        return abogadoRepo.save(existente);
    }

    public void eliminarAbogado(Long id) {
        Abogado abogado = abogadoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Abogado no encontrado: " + id));
        abogado.setActivo(false);
        abogadoRepo.save(abogado);
    }
}