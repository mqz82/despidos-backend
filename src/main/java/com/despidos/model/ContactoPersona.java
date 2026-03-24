package com.despidos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contacto_persona")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ContactoPersona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoContacto tipo;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "principal")
    private Boolean principal = false;

    @Column(name = "activo")
    private Boolean activo = true;

    public enum TipoContacto {
        EMAIL_LABORAL, EMAIL_PERSONAL,
        TELEFONO_MOVIL, TELEFONO_FIJO, TELEFONO_OFICINA
    }

}
