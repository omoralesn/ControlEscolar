package mx.gob.controlescolar.acceso.persistencia;

import mx.gob.controlescolar.acceso.dominio.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerfilRepositorio extends JpaRepository<Perfil, Long> {
    List<Perfil> findByInstitucionId(Long institucionId);
}
