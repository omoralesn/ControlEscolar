package mx.gob.controlescolar.acceso.persistencia;

import mx.gob.controlescolar.acceso.dominio.PerfilPermiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerfilPermisoRepositorio extends JpaRepository<PerfilPermiso, PerfilPermiso.Clave> {
    List<PerfilPermiso> findByPerfilId(Long perfilId);
}
