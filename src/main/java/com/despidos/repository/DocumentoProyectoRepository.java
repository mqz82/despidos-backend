package com.despidos.repository;

import com.despidos.model.DocumentoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentoProyectoRepository extends JpaRepository<DocumentoProyecto, Long> {

    List<DocumentoProyecto> findByProyectoId(Long proyectoId);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.proyecto.id = :proyectoId AND d.estado = 'PENDIENTE' AND d.obligatorio = true")
    List<DocumentoProyecto> findDocumentosObligatoriosPendientes(@Param("proyectoId") Long proyectoId);

    void deleteByProyectoId(Long proyectoId);
}
