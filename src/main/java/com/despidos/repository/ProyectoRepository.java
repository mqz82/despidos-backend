package com.despidos.repository;

import com.despidos.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    Optional<Proyecto> findByNumeroExpediente(String numeroExpediente);

    List<Proyecto> findByEstado(Proyecto.EstadoProyecto estado);

    // Proyectos cuya audiencia está dentro de X días Y la alerta NO fue enviada
    @Query("SELECT p FROM Proyecto p WHERE p.fechaAudiencia <= :fechaLimite " +
           "AND p.fechaAudiencia >= :hoy " +
           "AND p.alertaEnviada = false " +
           "AND p.estado IN ('ACTIVO', 'EN_JUICIO')")
    List<Proyecto> findProyectosParaAlertar(
            @Param("hoy") LocalDate hoy,
            @Param("fechaLimite") LocalDate fechaLimite
    );

    // Proyectos con documentos faltantes
    @Query("SELECT DISTINCT p FROM Proyecto p " +
           "JOIN p.documentos d " +
           "WHERE d.estado = 'PENDIENTE' " +
           "AND d.obligatorio = true " +
           "AND p.estado IN ('ACTIVO', 'EN_JUICIO')")
    List<Proyecto> findProyectosConDocumentosFaltantes();

    // Búsqueda libre
    @Query("SELECT p FROM Proyecto p WHERE " +
           "LOWER(p.nombreEmpleado) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.apellidoEmpleado) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.numeroExpediente) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.legajo) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Proyecto> buscar(@Param("termino") String termino);

    // Audiencias próximas (siguientes 60 días)
    @Query("SELECT p FROM Proyecto p WHERE p.fechaAudiencia BETWEEN :hoy AND :limite " +
           "AND p.estado IN ('ACTIVO', 'EN_JUICIO') ORDER BY p.fechaAudiencia ASC")
    List<Proyecto> findAudienciasProximas(
            @Param("hoy") LocalDate hoy,
            @Param("limite") LocalDate limite
    );
}
