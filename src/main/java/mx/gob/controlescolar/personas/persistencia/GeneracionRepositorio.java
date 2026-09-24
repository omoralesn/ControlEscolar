package mx.gob.controlescolar.personas.persistencia;

import java.util.List;
import mx.gob.controlescolar.personas.dominio.Generacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneracionRepositorio
extends JpaRepository<Generacion, Long> {
    public List<Generacion> findByInstitucionId(Long var1);
}
