package mx.gob.controlescolar.comun.persistencia;

import mx.gob.controlescolar.comun.dominio.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalidadRepositorio extends JpaRepository<Localidad, Long> {

    List<Localidad> findByCodigoPostalOrderByNombreAsc(String codigoPostal);
}
