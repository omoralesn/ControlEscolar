package mx.gob.controlescolar.acceso.persistencia;

import mx.gob.controlescolar.acceso.dominio.CentroTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CentroTrabajoRepositorio extends JpaRepository<CentroTrabajo, Long> {
    List<CentroTrabajo> findByInstitucionId(Long institucionId);

    List<CentroTrabajo> findByPlantelId(Long plantelId);

    Optional<CentroTrabajo> findByClave(String clave);
}
