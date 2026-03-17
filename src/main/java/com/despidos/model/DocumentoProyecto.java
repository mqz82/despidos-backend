package com.despidos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Lob;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentos_proyecto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_documento_id", nullable = false)
    private TipoDocumento tipoDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoDocumento estado = EstadoDocumento.PENDIENTE;

    @Column(name = "fecha_recepcion")
    private LocalDate fechaRecepcion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "numero_referencia")
    private String numeroReferencia;

    @Column(name = "responsable")
    private String responsable;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "url_archivo")
    private String urlArchivo;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    @Column(name = "obligatorio")
    private Boolean obligatorio = false;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Lob
    @Column(name = "archivo_contenido")
    private byte[] archivoContenido;

    @Column(name = "archivo_tipo")
    private String archivoTipo;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }

    public enum EstadoDocumento {
        PENDIENTE, EN_TRAMITE, RECIBIDO, VENCIDO, NO_APLICA
    }


}
