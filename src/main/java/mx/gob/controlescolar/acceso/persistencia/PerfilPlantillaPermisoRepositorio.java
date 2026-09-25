package mx.gob.controlescolar.acceso.persistencia;

import mx.gob.controlescolar.acceso.dominio.PerfilPlantillaPermiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerfilPlantillaPermisoRepositorio
        extends JpaRepository<PerfilPlantillaPermiso, PerfilPlantillaPermiso.Clave> {
    List<PerfilPlantillaPermiso> findByPerfilPlantillaId(Long perfilPlantillaId);
}
