package com.despidos.repository;

import com.despidos.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {
    List<TipoDocumento> findByActivoTrue();
    List<TipoDocumento> findByActivoTrueOrderByOrdenAsc();
    boolean existsByNombre(String nombre);
}
