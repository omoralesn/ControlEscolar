package mx.gob.controlescolar.academico.persistencia;

import mx.gob.controlescolar.academico.dominio.CalendarioEscolar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CalendarioRepositorio extends JpaRepository<CalendarioEscolar, Long> {
    List<CalendarioEscolar> findByCicloIdAndNivel(Long cicloId, String nivel);

    Optional<CalendarioEscolar> findByCicloIdAndNivelAndInstitucionId(Long cicloId, String nivel, Long institucionId);

    List<CalendarioEscolar> findByInstitucionId(Long institucionId);
}
