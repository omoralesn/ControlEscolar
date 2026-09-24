package mx.gob.controlescolar.personas.persistencia;

import mx.gob.controlescolar.personas.dominio.AlumnoTutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlumnoTutorRepositorio extends JpaRepository<AlumnoTutor, AlumnoTutor.Clave> {
    List<AlumnoTutor> findByTutorId(Long tutorId);

    List<AlumnoTutor> findByAlumnoId(Long alumnoId);
}
