package mx.gob.controlescolar.acceso.persistencia;

import mx.gob.controlescolar.acceso.dominio.PerfilPlantilla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilPlantillaRepositorio extends JpaRepository<PerfilPlantilla, Long> {
    Optional<PerfilPlantilla> findByNombre(String nombre);
}
