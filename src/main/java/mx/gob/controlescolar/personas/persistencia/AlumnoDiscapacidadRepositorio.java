package mx.gob.controlescolar.personas.persistencia;

import java.util.List;
import mx.gob.controlescolar.personas.dominio.AlumnoDiscapacidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlumnoDiscapacidadRepositorio
extends JpaRepository<AlumnoDiscapacidad, AlumnoDiscapacidad.Clave> {
    public List<AlumnoDiscapacidad> findByAlumnoId(Long var1);

    public void deleteByAlumnoId(Long var1);
}
