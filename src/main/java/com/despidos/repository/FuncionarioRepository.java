package com.despidos.repository;

import com.despidos.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    List<Funcionario> findByActivoTrue();
    Optional<Funcionario> findByPersonaRut(String rut);

    @Query("SELECT f FROM Funcionario f WHERE " +
            "LOWER(f.persona.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(f.persona.appPaterno) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(f.persona.rut) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Funcionario> buscar(String termino);
}
