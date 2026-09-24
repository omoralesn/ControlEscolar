package mx.gob.controlescolar.acceso.persistencia;

import mx.gob.controlescolar.acceso.dominio.UsuarioPerfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioPerfilRepositorio extends JpaRepository<UsuarioPerfil, UsuarioPerfil.Clave> {
    List<UsuarioPerfil> findByUsuarioId(Long usuarioId);
}
