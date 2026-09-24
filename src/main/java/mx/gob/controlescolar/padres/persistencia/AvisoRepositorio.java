package mx.gob.controlescolar.padres.persistencia;

import mx.gob.controlescolar.padres.dominio.Aviso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvisoRepositorio extends JpaRepository<Aviso, Long> {
    List<Aviso> findByInstitucionIdAndAlumnoIdIn(Long institucionId, List<Long> alumnoIds);
}
