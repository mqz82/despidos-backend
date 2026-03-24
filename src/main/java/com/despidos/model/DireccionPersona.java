package com.despidos.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "direccion_persona")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class DireccionPersona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private DireccionPersona.TipoDireccion tipo;

    @Column(name = "calle", nullable = false)
    private String calle;

    @Column(name = "numero_calle", nullable = false)
    private String numeroCalle;

    @Column(name = "departamento", nullable = false)
    private String departamento;

    @Column(name = "comuna", nullable = false)
    private String comuna;

    @Column(name = "ciudad", nullable = false)
    private String ciudad;

    @Column(name = "región", nullable = false)
    private String región;

    public enum TipoDireccion {
        DIRECCION_PARTICULAR, DIRECCION_LABORAL
    }

}
