package mx.gob.controlescolar.comun.persistencia;

import mx.gob.controlescolar.comun.dominio.AuditoriaEvento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepositorio extends JpaRepository<AuditoriaEvento, Long> {

    List<AuditoriaEvento> findByInstitucionIdOrderByOcurridoEnDesc(Long institucionId);
}
