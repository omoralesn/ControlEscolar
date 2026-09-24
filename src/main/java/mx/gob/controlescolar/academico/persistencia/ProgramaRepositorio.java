package mx.gob.controlescolar.academico.persistencia;

import mx.gob.controlescolar.academico.dominio.Programa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramaRepositorio extends JpaRepository<Programa, Long> {
    List<Programa> findByInstitucionId(Long institucionId);

    List<Programa> findByPlanVersionId(Long planVersionId);
}
