package mx.gob.controlescolar.academico.persistencia;

import mx.gob.controlescolar.academico.dominio.EstadoVersion;
import mx.gob.controlescolar.academico.dominio.PlanVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanVersionRepositorio extends JpaRepository<PlanVersion, Long> {
    List<PlanVersion> findByEstado(EstadoVersion estado);
}
