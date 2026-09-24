package mx.gob.controlescolar.inscripcion.persistencia;

import java.util.Optional;
import mx.gob.controlescolar.inscripcion.dominio.AsignacionDocente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsignacionDocenteRepositorio
extends JpaRepository<AsignacionDocente, Long> {
    public Optional<AsignacionDocente> findByGrupoIdAndProfesorIdAndAsignaturaClave(Long var1, Long var2, String var3);
}
