package mx.gob.controlescolar.acceso.aplicacion;

import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioDetalleService implements UserDetailsService {

    private final UsuarioRepositorio usuarios;
    private final PerfilService perfiles;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarios.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        usuario.conceder(perfiles.codigosDe(usuario.getId()));
        return usuario;
    }

    public Usuario actual(String login) {
        return usuarios.findByLogin(login).orElseThrow();
    }
}
