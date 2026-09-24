package mx.gob.controlescolar.personas.persistencia;

import java.util.List;
import mx.gob.controlescolar.personas.dominio.Dictamen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DictamenRepositorio
extends JpaRepository<Dictamen, Long> {
    public List<Dictamen> findByAlumnoId(Long var1);
}
