package mx.gob.controlescolar.academico.persistencia;

import mx.gob.controlescolar.academico.dominio.MomentoEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MomentoRepositorio extends JpaRepository<MomentoEvaluacion, Long> {
    List<MomentoEvaluacion> findByPlanVersionIdOrderByOrden(Long planVersionId);

    void deleteByPlanVersionId(Long planVersionId);
}
