package mx.gob.controlescolar.personas.persistencia;

import mx.gob.controlescolar.personas.dominio.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InscripcionRepositorio extends JpaRepository<Inscripcion, Long> {
    Optional<Inscripcion> findByAlumnoIdAndHistoricaFalse(Long alumnoId);

    List<Inscripcion> findByGrupoIdAndHistoricaFalse(Long grupoId);

    List<Inscripcion> findByAlumnoId(Long alumnoId);

    List<Inscripcion> findByInstitucionIdAndHistoricaFalse(Long institucionId);
}
