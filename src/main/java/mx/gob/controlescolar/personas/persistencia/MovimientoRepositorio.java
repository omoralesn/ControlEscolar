package mx.gob.controlescolar.personas.persistencia;

import mx.gob.controlescolar.personas.dominio.MovimientoAlumno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepositorio extends JpaRepository<MovimientoAlumno, Long> {
}
