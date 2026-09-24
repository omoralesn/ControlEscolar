package mx.gob.controlescolar.evaluacion.persistencia;

import mx.gob.controlescolar.evaluacion.dominio.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CalificacionRepositorio extends JpaRepository<Calificacion, Long> {
    boolean existsByPlanVersionId(Long planVersionId);

    boolean existsByPlanVersionIdAndAsignaturaClave(Long planVersionId, String asignaturaClave);

    boolean existsByPlanVersionIdAndComplementariaTrue(Long planVersionId);

    Optional<Calificacion> findByAlumnoIdAndAsignaturaClaveAndPeriodoOrdenAndMomentoId(
            Long alumnoId, String asignaturaClave, int periodoOrden, Long momentoId);

    List<Calificacion> findByAlumnoIdAndPlanVersionId(Long alumnoId, Long planVersionId);

    List<Calificacion> findByInstitucionIdAndAlumnoIdIn(Long institucionId, List<Long> alumnoIds);

    List<Calificacion> findByAlumnoIdAndAsignaturaClaveAndPeriodoOrdenAndMomentoIdOrderById(
            Long alumnoId, String asignaturaClave, int periodoOrden, Long momentoId);

    List<Calificacion> findByInstitucionId(Long institucionId);
}
