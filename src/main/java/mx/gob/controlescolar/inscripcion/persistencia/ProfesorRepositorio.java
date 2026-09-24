package mx.gob.controlescolar.inscripcion.persistencia;

import mx.gob.controlescolar.inscripcion.dominio.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfesorRepositorio extends JpaRepository<Profesor, Long> {
    List<Profesor> findByInstitucionId(Long institucionId);
}
