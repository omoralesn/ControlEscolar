package mx.gob.controlescolar.acceso.persistencia;

import mx.gob.controlescolar.acceso.dominio.Institucion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitucionRepositorio extends JpaRepository<Institucion, Long> {
}
