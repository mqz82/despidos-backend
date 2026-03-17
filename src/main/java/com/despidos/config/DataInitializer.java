package com.despidos.config;

import com.despidos.model.ConfiguracionSistema;
import com.despidos.model.TipoDocumento;
import com.despidos.repository.ConfiguracionSistemaRepository;
import com.despidos.repository.TipoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TipoDocumentoRepository tipoDocRepo;
    private final ConfiguracionSistemaRepository configRepo;

    @Override
    public void run(String... args) {
        inicializarTiposDocumento();
        inicializarConfiguracion();
        log.info("✅ Datos iniciales cargados correctamente");
    }

    private void inicializarTiposDocumento() {
        if (tipoDocRepo.count() > 0) return;

        List<TipoDocumento> tipos = List.of(
            crearTipo("Telegrama de Despido", "Telegrama fehaciente notificando el despido", true, TipoDocumento.CategoriaDocumento.LEGAL, 1),
            crearTipo("Liquidación Final", "Liquidación detallada de haberes finales e indemnización", true, TipoDocumento.CategoriaDocumento.CONTABLE, 2),
            crearTipo("Recibos de Sueldo (últimos 12 meses)", "Comprobantes de haberes de los últimos 12 meses", true, TipoDocumento.CategoriaDocumento.LABORAL, 3),
            crearTipo("Contrato Laboral", "Contrato de trabajo original firmado por ambas partes", true, TipoDocumento.CategoriaDocumento.LABORAL, 4),
            crearTipo("Legajo Personal", "Legajo completo del empleado con historial laboral", true, TipoDocumento.CategoriaDocumento.LABORAL, 5),
            crearTipo("Notificaciones Previas", "Apercibimientos, suspensiones y llamadas de atención anteriores", false, TipoDocumento.CategoriaDocumento.LEGAL, 6),
            crearTipo("Pericial Contable", "Informe pericial sobre los montos reclamados", false, TipoDocumento.CategoriaDocumento.CONTABLE, 7),
            crearTipo("Certificado de Servicios", "Certificado de servicios y remuneraciones (Art. 80 LCT)", true, TipoDocumento.CategoriaDocumento.LABORAL, 8),
            crearTipo("Telegramas Ley", "Telegramas del trabajador reclamando documentación", false, TipoDocumento.CategoriaDocumento.LEGAL, 9),
            crearTipo("Acta de Mediación", "Acta de la instancia de mediación prejudicial", false, TipoDocumento.CategoriaDocumento.LEGAL, 10),
            crearTipo("Recibo de Pago Indemnización", "Comprobante de pago de la indemnización", false, TipoDocumento.CategoriaDocumento.CONTABLE, 11),
            crearTipo("Historia Clínica / ART", "Documentación médica y de ART relevante", false, TipoDocumento.CategoriaDocumento.MEDICO, 12)
        );

        tipoDocRepo.saveAll(tipos);
        log.info("Tipos de documento inicializados: {}", tipos.size());
    }

    private void inicializarConfiguracion() {
        if (configRepo.count() > 0) return;

        List<ConfiguracionSistema> configs = List.of(
            crearConfig("DIAS_ANTICIPACION_ALERTA", "30", "Días de anticipación para enviar alerta antes de la audiencia", "NUMBER"),
            crearConfig("SEGUNDA_ALERTA_DIAS", "7", "Segunda alerta: días antes de la audiencia", "NUMBER"),
            crearConfig("EMAIL_ALERTAS_DESTINO", "legal@empresa.com", "Email principal para recibir alertas del sistema", "EMAIL"),
            crearConfig("NOMBRE_EMPRESA", "Mi Empresa S.A.", "Nombre de la empresa que aparece en los reportes", "STRING"),
            crearConfig("NOTIFICAR_DOCUMENTOS_FALTANTES", "true", "Enviar alerta solo cuando hay documentos faltantes", "BOOLEAN"),
            crearConfig("CRON_JOB_ALERTAS", "0 0 8 * * MON-FRI", "Expresión cron para el job de alertas automáticas", "CRON")
        );

        configRepo.saveAll(configs);
        log.info("Configuraciones inicializadas: {}", configs.size());
    }

    private TipoDocumento crearTipo(String nombre, String desc, boolean obligatorio,
                                     TipoDocumento.CategoriaDocumento cat, int orden) {
        TipoDocumento t = new TipoDocumento();
        t.setNombre(nombre);
        t.setDescripcion(desc);
        t.setObligatorio(obligatorio);
        t.setCategoria(cat);
        t.setOrden(orden);
        t.setActivo(true);
        return t;
    }

    private ConfiguracionSistema crearConfig(String clave, String valor, String desc, String tipo) {
        ConfiguracionSistema c = new ConfiguracionSistema();
        c.setClave(clave);
        c.setValor(valor);
        c.setDescripcion(desc);
        c.setTipo(tipo);
        return c;
    }
}
