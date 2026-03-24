package com.despidos.service;

import com.despidos.model.ContactoPersona;
import com.despidos.model.Persona;
import com.despidos.repository.*;
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
public class PersonaService {

    private final PersonaRepository personaRepo;
    private final ContactoPersonaRepository contactoRepo;

    // ==========================================
    // CRUD PERSONA
    // ==========================================

    public Persona crearPersona(Persona persona){
        if (personaRepo.existsByRut(persona.getRut())){
            throw new RuntimeException("Ya existe una persona con RUT: "+ persona.getRut());
        }

        log.info("Creando persona : "+  persona.getRut(),persona.getNombres(), persona.getAppMaterno());

        return personaRepo.save(persona);
    }

    @Transactional(readOnly = true)
    public List<Persona> listarPersonas() {
        return personaRepo.findByActivoTrue();
    }

    public Persona ActulaizaPersona (Long id, Persona personaActulizada ){
        Persona personaExistente = personaRepo.findById(id).orElseThrow(()-> new RuntimeException("Persona no encontrada : "  + id ));

        personaExistente.setNombres(personaActulizada.getNombres());
        personaExistente.setAppPaterno(personaActulizada.getAppPaterno());
        personaExistente.setAppMaterno(personaActulizada.getAppMaterno());
        personaExistente.setRut(personaActulizada.getRut());
        personaExistente.setGenero(personaActulizada.getGenero());
        personaExistente.setFechaNacimiento(personaActulizada.getFechaNacimiento());
    return personaRepo.save(personaExistente);
    }

    @Transactional(readOnly = true)
    public Optional<Persona> obtenerPersona(Long id) {
        return personaRepo.findById(id);
    }

    public Persona actualizarPersona(Long id, Persona personaActualizada) {
        Persona existente = personaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada: " + id));

        existente.setNombres(personaActualizada.getNombres());
        existente.setAppPaterno(personaActualizada.getAppPaterno());
        existente.setAppMaterno(personaActualizada.getAppMaterno());
        existente.setRut(personaActualizada.getRut());
        existente.setGenero(personaActualizada.getGenero());
        existente.setFechaNacimiento(personaActualizada.getFechaNacimiento());

        return personaRepo.save(existente);
    }

    public void eliminarPersona(Long id) {
        Persona persona = personaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada: " + id));
        persona.setActivo(false);
        personaRepo.save(persona);
    }

    // ==========================================
    // CONTACTOS
    // ==========================================

    public ContactoPersona agregarContacto(Long personaId, ContactoPersona contacto) {
        Persona persona = personaRepo.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada: " + personaId));
        contacto.setPersona(persona);
        return contactoRepo.save(contacto);
    }

    @Transactional(readOnly = true)
    public List<ContactoPersona> listarContactos(Long personaId) {
        return contactoRepo.findByPersonaIdAndActivoTrue(personaId);
    }

    public void eliminarContacto(Long contactoId) {
        ContactoPersona contacto = contactoRepo.findById(contactoId)
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado: " + contactoId));
        contacto.setActivo(false);
        contactoRepo.save(contacto);
    }

    @Transactional(readOnly = true)
    public Optional<Persona> buscarPorRut(String rut) {
        return personaRepo.findByRut(rut);
    }
}
