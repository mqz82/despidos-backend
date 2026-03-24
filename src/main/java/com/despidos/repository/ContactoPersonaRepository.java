package com.despidos.repository;

import com.despidos.model.ContactoPersona;
import com.despidos.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactoPersonaRepository extends JpaRepository<ContactoPersona, Long> {
    List<ContactoPersona> findByPersonaId(Long personaId);
    List<ContactoPersona> findByPersonaIdAndActivoTrue(Long personaId);
}

