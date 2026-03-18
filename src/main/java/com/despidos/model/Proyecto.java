package com.despidos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proyectos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === DATOS DEL EXPEDIENTE ===
    @NotBlank(message = "El nombre de expediente es requerido")
    @Column(name = "nombre_expediente", unique = true)
    private String nombreExpediente;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoProyecto estado = EstadoProyecto.ACTIVO;

    // === DATOS DEL EMPLEADO ===
    @NotBlank(message = "El nombre del empleado es requerido")
    @Column(name = "nombre_empleado")
    private String nombreEmpleado;

    @Column(name = "apellido_empleado")
    private String apellidoEmpleado;

    @Column(name = "legajo")
    private String legajo;

    @Column(name = "area_departamento")
    private String areaDepartamento;

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "fecha_despido")
    private LocalDate fechaDespido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_despido")
    private TipoDespido tipoDespido;

    @Column(name = "monto_indemnizacion", precision = 15, scale = 2)
    private BigDecimal montoIndemnizacion;

    @Column(name = "rut_Empleado")
    private String rutEmpleado;

    @Column(name = "email_empleado")
    private String emailEmpleado;

    // === DATOS DEL JUICIO ===
    @NotNull(message = "La fecha de audiencia es requerida")
    @Column(name = "fecha_audiencia")
    private LocalDate fechaAudiencia;

    @Column(name = "Dirección_trabajo")
    private String direccionTrabajo;

    @Column(name = "numero_causa")
    private String numeroCausa;

    @Column(name = "abogado_empresa")
    private String abogadoEmpresa;

    @Column(name = "abogado_empleado")
    private String abogadoEmpleado;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    // === CONFIGURACIÓN DE ALERTAS ===
    @Column(name = "dias_anticipacion_alerta")
    private Integer diasAnticipacionAlerta = 30;

    @Column(name = "email_alerta")
    private String emailAlerta;

    @Column(name = "alerta_enviada")
    private Boolean alertaEnviada = false;

    @Column(name = "ultima_alerta_enviada")
    private LocalDateTime ultimaAlertaEnviada;

    // === METADATOS ===
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "rut_abogado_Empleado")
    private String rutAbogadoEmpleado;

    @Column(name = "rut_Abogado_Empresa")
    private String rutAbogadoEmpresa;

    // === DOCUMENTOS ===
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DocumentoProyecto> documentos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }

    // === ENUMS ===
    public enum EstadoProyecto {
        ACTIVO, EN_JUICIO, RESUELTO, ARCHIVADO
    }

    public enum TipoDespido {
        DESVINCULACION_VOLUNTARIA, MUTUO_ACUERDO, DESVINCULACION_INVOLUNTARIA,
        CAUSALES_DISCIPLINARIAS, CAUSALES_OBJETIVAS, OTRAS_CAUSALES
    }
}
