package com.despidos.service;

import com.despidos.model.ConfiguracionSistema;
import com.despidos.repository.ConfiguracionSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConfiguracionService {

    private final ConfiguracionSistemaRepository repo;

    public static final String DIAS_ANTICIPACION = "DIAS_ANTICIPACION_ALERTA";
    public static final String EMAIL_DESTINO = "EMAIL_ALERTAS_DESTINO";
    public static final String NOMBRE_EMPRESA = "NOMBRE_EMPRESA";
    public static final String SEGUNDA_ALERTA_DIAS = "SEGUNDA_ALERTA_DIAS";

    @Transactional(readOnly = true)
    public List<ConfiguracionSistema> listarConfiguraciones() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ConfiguracionSistema> obtenerPorClave(String clave) {
        return repo.findByClave(clave);
    }

    public ConfiguracionSistema guardar(ConfiguracionSistema config) {
        Optional<ConfiguracionSistema> existente = repo.findByClave(config.getClave());
        if (existente.isPresent()) {
            existente.get().setValor(config.getValor());
            return repo.save(existente.get());
        }
        return repo.save(config);
    }

    public ConfiguracionSistema actualizarValor(String clave, String valor) {
        ConfiguracionSistema config = repo.findByClave(clave)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada: " + clave));
        config.setValor(valor);
        return repo.save(config);
    }

    // Helpers para obtener valores tipados
    public int getDiasAnticipacion() {
        return repo.findByClave(DIAS_ANTICIPACION)
                .map(c -> Integer.parseInt(c.getValor()))
                .orElse(30);
    }

    public String getEmailDestino() {
        return repo.findByClave(EMAIL_DESTINO)
                .map(ConfiguracionSistema::getValor)
                .orElse("legal@empresa.com");
    }

    public String getNombreEmpresa() {
        return repo.findByClave(NOMBRE_EMPRESA)
                .map(ConfiguracionSistema::getValor)
                .orElse("Mi Empresa");
    }
}
