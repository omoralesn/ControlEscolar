package mx.gob.controlescolar.acceso.aplicacion;

import mx.gob.controlescolar.acceso.dominio.Plantel;
import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.persistencia.PlantelRepositorio;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccesoService {

    private final PlantelRepositorio planteles;
    private final UsuarioRepositorio usuarios;
    private final AuditoriaService auditoria;
    private final PasswordEncoder passwordEncoder;

    public List<Plantel> planteles() {
        return planteles.findAll();
    }

    public List<Usuario> deLaEscuela(Long institucionId) {
        return usuarios.findByInstitucionId(institucionId);
    }

    @Transactional
    public void suspenderPlantel(Long plantelId) {
        plantel(plantelId).suspender();
    }

    @Transactional
    public void activarPlantel(Long plantelId) {
        plantel(plantelId).activar();
    }

    @Transactional
    public void definirVigencia(Long usuarioId, LocalDate desde, LocalDate hasta) {
        Usuario usuario = cuentaDeEscuela(usuarioId, null);
        usuario.definirVigencia(desde, hasta);
        auditoria.registrar(usuario.getInstitucion().getId(), "VIGENCIA_USUARIO",
                "desde=" + desde + ",hasta=" + hasta, usuario.getUsername(), null);
    }

    @Transactional
    public void suspenderUsuario(Long usuarioId) {
        Usuario usuario = cuentaDeEscuela(usuarioId, null);
        usuario.suspenderCuenta();
        auditoria.registrar(usuario.getInstitucion().getId(), "SUSPENDER_USUARIO", usuario.getNombre(),
                usuario.getUsername(), null);
    }

    @Transactional
    public void activarUsuario(Long usuarioId) {
        Usuario usuario = cuentaDeEscuela(usuarioId, null);
        usuario.activarCuenta();
        auditoria.registrar(usuario.getInstitucion().getId(), "ACTIVAR_USUARIO", usuario.getNombre(),
                usuario.getUsername(), null);
    }

    @Transactional
    public void bloquear(Long usuarioId, Long institucionId) {
        Usuario usuario = cuentaDeEscuela(usuarioId, institucionId);
        usuario.bloquear();
        auditoria.registrar(usuario.getInstitucion().getId(), "BLOQUEAR_USUARIO", usuario.getNombre(),
                usuario.getUsername(), null);
    }

    @Transactional
    public void desbloquear(Long usuarioId, Long institucionId) {
        Usuario usuario = cuentaDeEscuela(usuarioId, institucionId);
        usuario.desbloquear();
        auditoria.registrar(usuario.getInstitucion().getId(), "DESBLOQUEAR_USUARIO", usuario.getNombre(),
                usuario.getUsername(), null);
    }

    @Transactional
    public void reponerCredencial(Long usuarioId, Long institucionId, String claveNueva) {
        if (claveNueva == null || claveNueva.trim().length() < 6) {
            throw new NegocioException("La credencial debe tener al menos 6 caracteres");
        }
        Usuario usuario = cuentaDeEscuela(usuarioId, institucionId);
        usuario.reponerCredenciales(passwordEncoder.encode(claveNueva.trim()));
        auditoria.registrar(usuario.getInstitucion().getId(), "CREDENCIAL_USUARIO", "Credencial repuesta",
                usuario.getUsername(), null);
    }

    @Transactional
    public void vencerCredencial(Long usuarioId, Long institucionId) {
        Usuario usuario = cuentaDeEscuela(usuarioId, institucionId);
        usuario.vencerCredenciales();
        auditoria.registrar(usuario.getInstitucion().getId(), "CREDENCIAL_VENCIDA", usuario.getNombre(),
                usuario.getUsername(), null);
    }

    private Plantel plantel(Long plantelId) {
        return planteles.findById(plantelId).orElseThrow(() -> new NegocioException("El plantel no existe"));
    }

    private Usuario cuentaDeEscuela(Long usuarioId, Long institucionId) {
        Usuario usuario = usuarios.findById(usuarioId).orElseThrow(() -> new NegocioException("El usuario no existe"));
        if (usuario.esSuper() || usuario.esPlataforma() || usuario.getInstitucion() == null) {
            throw new NegocioException("Solo se administra la cuenta de un usuario de escuela");
        }
        if (institucionId != null && !institucionId.equals(usuario.getInstitucion().getId())) {
            throw new NegocioException("El usuario no pertenece a la escuela");
        }
        return usuario;
    }
}
