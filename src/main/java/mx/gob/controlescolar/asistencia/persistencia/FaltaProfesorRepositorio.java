package mx.gob.controlescolar.asistencia.persistencia;

import mx.gob.controlescolar.asistencia.dominio.FaltaProfesor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaltaProfesorRepositorio extends JpaRepository<FaltaProfesor, Long> {
    List<FaltaProfesor> findByInstitucionIdAndProfesorId(Long institucionId, Long profesorId);
}
