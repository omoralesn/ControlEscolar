package mx.gob.controlescolar.inscripcion.persistencia;

import mx.gob.controlescolar.inscripcion.dominio.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoRepositorio extends JpaRepository<Grupo, Long> {
    List<Grupo> findByInstitucionId(Long institucionId);
}
