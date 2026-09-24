package mx.gob.controlescolar.acceso.persistencia;

import mx.gob.controlescolar.acceso.dominio.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByLogin(String login);

    java.util.List<Usuario> findByInstitucionId(Long institucionId);
}
