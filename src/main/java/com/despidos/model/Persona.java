package com.despidos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "personas")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombres", nullable = false)
    private String nombres;

    @Column(name = "app_paterno", nullable = false)
    private String appPaterno;

    @Column(name = "app_materno", nullable = true)
    private String appMaterno;

    @Column(name = "rut", unique = true)
    private String rut;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero")
    private Genero genero;

    @Column(name = "fecha_nacimiento")
    private java.time.LocalDate fechaNacimiento;

    @Column(name = "activo")
    private Boolean activo = true;

    public enum Genero {
        MASCULINO, FEMENINO, OTRO, PREFIERO_NO_DECIR
    }
}
