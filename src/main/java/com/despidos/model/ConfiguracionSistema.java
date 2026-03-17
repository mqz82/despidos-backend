package com.despidos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "configuracion_sistema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clave", unique = true, nullable = false)
    private String clave;

    @Column(name = "valor", columnDefinition = "TEXT")
    private String valor;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "tipo")
    private String tipo; // STRING, NUMBER, BOOLEAN, EMAIL, CRON

    // Configuraciones disponibles:
    // DIAS_ANTICIPACION_ALERTA: días antes del juicio para alertar (default: 30)
    // EMAIL_ALERTAS_DESTINO: email donde se envían alertas
    // CRON_JOB_ALERTAS: expresión cron para job de alertas
    // NOMBRE_EMPRESA: nombre de la empresa
    // NOTIFICAR_DOCUMENTOS_FALTANTES: true/false
    // SEGUNDA_ALERTA_DIAS: segunda alerta X días antes
}
