package com.despidos.repository;

import com.despidos.model.Abogado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AbogadoRepository extends JpaRepository<Abogado, Long> {
    List<Abogado> findByActivoTrue();
    Optional<Abogado> findByPersonaRut(String rut);

    @Query("SELECT a FROM Abogado a WHERE " +
            "LOWER(a.persona.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(a.persona.appPaterno) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(a.persona.rut) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Abogado> buscar(String termino);
}
