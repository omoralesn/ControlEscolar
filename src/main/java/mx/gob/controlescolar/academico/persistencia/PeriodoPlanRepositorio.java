package mx.gob.controlescolar.academico.persistencia;

import mx.gob.controlescolar.academico.dominio.PeriodoPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeriodoPlanRepositorio extends JpaRepository<PeriodoPlan, Long> {
    List<PeriodoPlan> findByPlanVersionIdOrderByOrden(Long planVersionId);
}
