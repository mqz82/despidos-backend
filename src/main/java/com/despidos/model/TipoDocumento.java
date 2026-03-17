package com.despidos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tipos_documento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del tipo de documento es requerido")
    @Column(name = "nombre", unique = true)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "obligatorio")
    private Boolean obligatorio = false;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "orden")
    private Integer orden = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private CategoriaDocumento categoria;

    public enum CategoriaDocumento {
        LABORAL, LEGAL, CONTABLE, MEDICO, OTRO
    }
}
