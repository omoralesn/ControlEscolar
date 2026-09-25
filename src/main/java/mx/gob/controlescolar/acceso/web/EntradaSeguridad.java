package mx.gob.controlescolar.acceso.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntradaSeguridad implements AuthenticationFailureHandler, AuthenticationSuccessHandler {

    private final UsuarioRepositorio usuarios;

    @Override
    @Transactional
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws java.io.IOException {
        String aviso = mensaje(exception);
        String login = request.getParameter("username");
        if (!(exception instanceof LockedException) && !(exception instanceof DisabledException)
                && !(exception instanceof AccountExpiredException) && !(exception instanceof CredentialsExpiredException)
                && login != null) {
            var encontrado = usuarios.findByLogin(login);
            if (encontrado.isPresent()) {
                Usuario usuario = encontrado.get();
                usuario.registrarIntentoFallido();
                usuarios.save(usuario);
                if (!usuario.isAccountNonLocked()) {
                    aviso = "La cuenta está bloqueada.";
                }
            }
        }
        request.getSession().setAttribute("entrada", aviso);
        response.sendRedirect(request.getContextPath() + "/entrar");
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws java.io.IOException {
        if (authentication.getPrincipal() instanceof Usuario usuario) {
            usuario.reiniciarIntentos();
            usuarios.save(usuario);
        }
        response.sendRedirect(request.getContextPath() + "/panel");
    }

    private static String mensaje(AuthenticationException exception) {
        if (exception instanceof LockedException) {
            return "La cuenta está bloqueada.";
        }
        if (exception instanceof AccountExpiredException) {
            return "La vigencia de la cuenta terminó.";
        }
        if (exception instanceof CredentialsExpiredException) {
            return "La credencial venció. La escuela puede reponerla.";
        }
        if (exception instanceof DisabledException) {
            return "La cuenta no está habilitada.";
        }
        return "No se pudo entrar.";
    }
}
