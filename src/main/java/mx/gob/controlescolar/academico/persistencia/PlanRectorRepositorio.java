package mx.gob.controlescolar.academico.persistencia;

import mx.gob.controlescolar.academico.dominio.PlanRector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRectorRepositorio extends JpaRepository<PlanRector, Long> {
}
