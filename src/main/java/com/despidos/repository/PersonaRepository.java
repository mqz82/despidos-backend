package com.despidos.repository;

import com.despidos.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
     Optional<Persona> findByRut(String rut);
     boolean existsByRut(String rut);
     List<Persona> findByActivoTrue();

 }

