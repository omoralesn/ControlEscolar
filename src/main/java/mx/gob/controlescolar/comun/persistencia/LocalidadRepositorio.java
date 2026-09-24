package mx.gob.controlescolar.comun.persistencia;

import mx.gob.controlescolar.comun.dominio.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalidadRepositorio extends JpaRepository<Localidad, Long> {
}
