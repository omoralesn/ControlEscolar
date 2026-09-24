package mx.gob.controlescolar.academico.persistencia;

import mx.gob.controlescolar.academico.dominio.AsignaturaPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsignaturaPlanRepositorio extends JpaRepository<AsignaturaPlan, Long> {
    List<AsignaturaPlan> findByPlanVersionId(Long planVersionId);
}
