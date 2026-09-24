package mx.gob.controlescolar.personas.persistencia;

import mx.gob.controlescolar.personas.dominio.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlumnoRepositorio extends JpaRepository<Alumno, Long> {
    List<Alumno> findByInstitucionId(Long institucionId);
}
