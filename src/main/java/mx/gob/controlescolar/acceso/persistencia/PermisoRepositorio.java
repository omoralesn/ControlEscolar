package mx.gob.controlescolar.acceso.persistencia;

import mx.gob.controlescolar.acceso.dominio.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermisoRepositorio extends JpaRepository<Permiso, Long> {
    Optional<Permiso> findByCodigo(String codigo);
}
