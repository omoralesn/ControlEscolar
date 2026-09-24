package mx.gob.controlescolar.inscripcion.persistencia;

import mx.gob.controlescolar.inscripcion.dominio.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioRepositorio extends JpaRepository<Horario, Long> {
    List<Horario> findByProfesorIdAndDia(Long profesorId, String dia);

    List<Horario> findByInstitucionId(Long institucionId);
}
