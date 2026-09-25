package mx.gob.controlescolar.asistencia.persistencia;

import mx.gob.controlescolar.asistencia.dominio.AsistenciaAlumno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsistenciaAlumnoRepositorio extends JpaRepository<AsistenciaAlumno, Long> {
    List<AsistenciaAlumno> findByAlumnoIdAndPresenteFalse(Long alumnoId);

    List<AsistenciaAlumno> findByAlumnoId(Long alumnoId);

    List<AsistenciaAlumno> findByListaId(Long listaId);

    Optional<AsistenciaAlumno> findByListaIdAndAlumnoId(Long listaId, Long alumnoId);
}
