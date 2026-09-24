package mx.gob.controlescolar.acceso.aplicacion;

import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.dominio.Perfil;
import mx.gob.controlescolar.acceso.dominio.PerfilPermiso;
import mx.gob.controlescolar.acceso.dominio.Permiso;
import mx.gob.controlescolar.acceso.dominio.PermisoCatalogo;
import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.dominio.UsuarioPerfil;
import mx.gob.controlescolar.acceso.persistencia.InstitucionRepositorio;
import mx.gob.controlescolar.acceso.persistencia.PerfilPermisoRepositorio;
import mx.gob.controlescolar.acceso.persistencia.PerfilRepositorio;
import mx.gob.controlescolar.acceso.persistencia.PermisoRepositorio;
import mx.gob.controlescolar.acceso.persistencia.UsuarioPerfilRepositorio;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepositorio perfiles;
    private final PermisoRepositorio permisos;
    private final PerfilPermisoRepositorio enlaces;
    private final UsuarioPerfilRepositorio asignaciones;
    private final UsuarioRepositorio usuarios;
    private final InstitucionRepositorio instituciones;
    private final ModuloGuardia modulos;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Perfil crear(Long institucionId, String nombre, Set<String> codigos) {
        for (String codigo : codigos) {
            modulos.exigir(institucionId, PermisoCatalogo.moduloDe(codigo));
        }
        Institucion institucion = instituciones.findById(institucionId)
                .orElseThrow(() -> new NegocioException("La escuela no existe"));
        return guardar(institucion, nombre, codigos);
    }

    @Transactional
    public void otorgarAdministrador(Institucion institucion, Usuario usuario, Set<String> codigos) {
        Perfil perfil = guardar(institucion, "Administrador escolar", codigos);
        asignaciones.save(new UsuarioPerfil(usuario.getId(), perfil.getId()));
    }

    @Transactional
    public Usuario crearUsuario(Long institucionId, String login, String clave, String nombre, Long perfilId) {
        Perfil perfil = perfiles.findById(perfilId)
                .orElseThrow(() -> new NegocioException("El perfil no existe"));
        if (!perfil.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El perfil no pertenece a la escuela");
        }
        Institucion institucion = instituciones.findById(institucionId).orElseThrow();
        Usuario usuario = usuarios.save(new Usuario(institucion, login, passwordEncoder.encode(clave), nombre));
        asignaciones.save(new UsuarioPerfil(usuario.getId(), perfil.getId()));
        return usuario;
    }

    public void exigir(Long usuarioId, String codigo) {
        if (!autorizado(usuarioId, codigo)) {
            throw new NegocioException("El perfil no autoriza " + codigo);
        }
    }

    public boolean autorizado(Long usuarioId, String codigo) {
        for (UsuarioPerfil asignacion : asignaciones.findByUsuarioId(usuarioId)) {
            for (PerfilPermiso enlace : enlaces.findByPerfilId(asignacion.getPerfilId())) {
                Permiso permiso = permisos.findById(enlace.getPermisoId()).orElseThrow();
                if (permiso.getCodigo().equals(codigo)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Perfil> deLaEscuela(Long institucionId) {
        return perfiles.findByInstitucionId(institucionId);
    }

    public List<PermisoCatalogo.Def> disponibles(Long institucionId) {
        return PermisoCatalogo.TODOS.stream()
                .filter(def -> modulos.activo(institucionId, def.modulo()))
                .toList();
    }

    @Transactional
    public void retirarFueraDeModulos(Long institucionId) {
        Set<Modulo> activos = modulos.activos(institucionId);
        for (Perfil perfil : perfiles.findByInstitucionId(institucionId)) {
            for (PerfilPermiso enlace : List.copyOf(enlaces.findByPerfilId(perfil.getId()))) {
                Permiso permiso = permisos.findById(enlace.getPermisoId()).orElseThrow();
                if (!activos.contains(PermisoCatalogo.moduloDe(permiso.getCodigo()))) {
                    enlaces.delete(enlace);
                }
            }
        }
    }

    private Perfil guardar(Institucion institucion, String nombre, Set<String> codigos) {
        Perfil perfil = perfiles.save(new Perfil(institucion, nombre));
        for (String codigo : new HashSet<>(codigos)) {
            Permiso permiso = permisos.findByCodigo(codigo)
                    .orElseThrow(() -> new NegocioException("El permiso no existe"));
            enlaces.save(new PerfilPermiso(perfil.getId(), permiso.getId()));
        }
        return perfil;
    }
}
