package mx.gob.controlescolar.comun.persistencia;

import mx.gob.controlescolar.comun.dominio.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MunicipioRepositorio extends JpaRepository<Municipio, Long> {
}
