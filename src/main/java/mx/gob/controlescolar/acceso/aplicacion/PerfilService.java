package mx.gob.controlescolar.acceso.aplicacion;

import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.dominio.Perfil;
import mx.gob.controlescolar.acceso.dominio.PerfilPermiso;
import mx.gob.controlescolar.acceso.dominio.PerfilPlantilla;
import mx.gob.controlescolar.acceso.dominio.PerfilPlantillaPermiso;
import mx.gob.controlescolar.acceso.dominio.Permiso;
import mx.gob.controlescolar.acceso.dominio.PermisoCatalogo;
import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.dominio.UsuarioPerfil;
import mx.gob.controlescolar.acceso.persistencia.InstitucionRepositorio;
import mx.gob.controlescolar.acceso.persistencia.PerfilPermisoRepositorio;
import mx.gob.controlescolar.acceso.persistencia.PerfilRepositorio;
import mx.gob.controlescolar.acceso.persistencia.PerfilPlantillaPermisoRepositorio;
import mx.gob.controlescolar.acceso.persistencia.PerfilPlantillaRepositorio;
import mx.gob.controlescolar.acceso.persistencia.PermisoRepositorio;
import mx.gob.controlescolar.acceso.persistencia.UsuarioPerfilRepositorio;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepositorio perfiles;
    private final PermisoRepositorio permisos;
    private final PerfilPermisoRepositorio enlaces;
    private final PerfilPlantillaRepositorio plantillas;
    private final PerfilPlantillaPermisoRepositorio plantillaPermisos;
    private final UsuarioPerfilRepositorio asignaciones;
    private final UsuarioRepositorio usuarios;
    private final InstitucionRepositorio instituciones;
    private final ModuloGuardia modulos;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoria;

    @Transactional
    public Perfil crear(Long institucionId, String nombre, Set<String> codigos) {
        for (String codigo : codigos) {
            modulos.exigir(institucionId, PermisoCatalogo.moduloDe(codigo));
        }
        Institucion institucion = instituciones.findById(institucionId)
                .orElseThrow(() -> new NegocioException("La escuela no existe"));
        Perfil perfil = guardar(institucion, nombre, codigos);
        auditoria.registrar(institucionId, "ALTA_PERFIL", nombre, null, nombre);
        return perfil;
    }

    @Transactional
    public void otorgarAdministrador(Institucion institucion, Usuario usuario, Set<String> codigos) {
        Perfil perfil = prepararAdministrador(institucion, codigos);
        asignarPerfil(usuario.getId(), perfil.getId());
    }

    @Transactional
    public void asignarPerfil(Long usuarioId, Long perfilId) {
        asignaciones.save(new UsuarioPerfil(usuarioId, perfilId));
    }

    /** Perfil base de la escuela; siempre puede consultar y capturar usuarios. */
    @Transactional
    public Perfil prepararAdministrador(Institucion institucion, Set<String> codigos) {
        Set<String> completos = new HashSet<>(codigos);
        completos.add("USUARIOS_CONSULTAR");
        completos.add("USUARIOS_CAPTURAR");
        return perfiles.findByInstitucionIdAndNombre(institucion.getId(), "Administrador escolar")
                .orElseGet(() -> guardar(institucion, "Administrador escolar", completos));
    }

    @Transactional
    public Usuario crearUsuario(Long institucionId, String login, String clave, String nombre, Long perfilId) {
        return crearUsuario(institucionId, login, clave, nombre, perfilId, "ALTA_USUARIO", null);
    }

    /**
     * Alta de usuario hecha por el super: primeros de registro u otros perfiles en casos particulares.
     * Siempre queda en bitácora con acción ALTA_USUARIO_SUPER.
     */
    @Transactional
    public Usuario crearUsuarioPorSuper(Long institucionId, String login, String clave, String nombre,
                                        Long perfilId, boolean excepcional) {
        String detalle = excepcional
                ? "Alta excepcional por super (no es el flujo habitual de la escuela)"
                : "Primer(os) usuario(s) de registro";
        return crearUsuario(institucionId, login, clave, nombre, perfilId, "ALTA_USUARIO_SUPER", detalle);
    }

    private Usuario crearUsuario(Long institucionId, String login, String clave, String nombre, Long perfilId,
                                 String accion, String detalleExtra) {
        Perfil perfil = perfiles.findById(perfilId)
                .orElseThrow(() -> new NegocioException("El perfil no existe"));
        if (!perfil.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El perfil no pertenece a la escuela");
        }
        Institucion institucion = instituciones.findById(institucionId).orElseThrow();
        Usuario usuario = usuarios.save(new Usuario(institucion, login, passwordEncoder.encode(clave), nombre));
        asignaciones.save(new UsuarioPerfil(usuario.getId(), perfil.getId()));
        String detalle = detalleExtra == null ? nombre : detalleExtra + " · " + nombre;
        auditoria.registrar(institucionId, accion, detalle, login, perfil.getNombre());
        return usuario;
    }

    @Transactional
    public void reasignar(Long institucionId, Long usuarioId, Long perfilId) {
        Usuario usuario = usuarios.findById(usuarioId)
                .orElseThrow(() -> new NegocioException("El usuario no existe"));
        if (usuario.getInstitucion() == null || !institucionId.equals(usuario.getInstitucion().getId())) {
            throw new NegocioException("El usuario no pertenece a la escuela");
        }
        Perfil perfil = perfiles.findById(perfilId)
                .orElseThrow(() -> new NegocioException("El perfil no existe"));
        if (!perfil.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El perfil no pertenece a la escuela");
        }
        String anterior = perfilDe(usuarioId).map(Perfil::getNombre).orElse("sin perfil");
        asignaciones.deleteByUsuarioId(usuarioId);
        asignaciones.flush();
        asignaciones.save(new UsuarioPerfil(usuarioId, perfilId));
        auditoria.registrar(institucionId, "REASIGNAR_PERFIL",
                anterior + " → " + perfil.getNombre(), usuario.getUsername(), perfil.getNombre());
    }

    public java.util.Optional<Perfil> perfilDe(Long usuarioId) {
        return asignaciones.findByUsuarioId(usuarioId).stream()
                .map(enlace -> perfiles.findById(enlace.getPerfilId()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .findFirst();
    }

    public List<FilaUsuario> filas(Long institucionId) {
        return usuarios.findByInstitucionId(institucionId).stream()
                .map(usuario -> {
                    Perfil perfil = perfilDe(usuario.getId()).orElse(null);
                    return new FilaUsuario(usuario, perfil == null ? null : perfil.getId(),
                            perfil == null ? "" : perfil.getNombre());
                })
                .toList();
    }

    @Transactional
    public List<PerfilPlantilla> plantillas() {
        asegurarPlantillas();
        return plantillas.findAll();
    }

    @Transactional
    public PerfilPlantilla crearPlantilla(String nombre, Set<String> codigos) {
        if (plantillas.findByNombre(nombre).isPresent()) {
            throw new NegocioException("Ya existe una plantilla con ese nombre");
        }
        PerfilPlantilla plantilla = plantillas.save(new PerfilPlantilla(nombre));
        for (String codigo : new HashSet<>(codigos)) {
            Permiso permiso = permisos.findByCodigo(codigo)
                    .orElseThrow(() -> new NegocioException("El permiso no existe"));
            plantillaPermisos.save(new PerfilPlantillaPermiso(plantilla.getId(), permiso.getId()));
        }
        return plantilla;
    }

    /**
     * Copia la plantilla a la escuela con los permisos de módulos activos.
     * Si la escuela ya tiene un perfil con ese nombre, se conserva (ajuste local).
     */
    @Transactional
    public Perfil aplicarPlantilla(Long institucionId, Long plantillaId) {
        PerfilPlantilla plantilla = plantillas.findById(plantillaId)
                .orElseThrow(() -> new NegocioException("La plantilla no existe"));
        return perfiles.findByInstitucionIdAndNombre(institucionId, plantilla.getNombre())
                .orElseGet(() -> crearDesdePlantilla(institucionId, plantilla));
    }

    private Perfil crearDesdePlantilla(Long institucionId, PerfilPlantilla plantilla) {
        Set<String> codigos = new HashSet<>();
        for (PerfilPlantillaPermiso enlace : plantillaPermisos.findByPerfilPlantillaId(plantilla.getId())) {
            String codigo = permisos.findById(enlace.getPermisoId()).orElseThrow().getCodigo();
            if (modulos.activo(institucionId, PermisoCatalogo.moduloDe(codigo))) {
                codigos.add(codigo);
            }
        }
        if (codigos.isEmpty()) {
            throw new NegocioException("La escuela no tiene activos los módulos de esa plantilla");
        }
        Perfil perfil = crear(institucionId, plantilla.getNombre(), codigos);
        auditoria.registrar(institucionId, "APLICAR_PLANTILLA", plantilla.getNombre(), null, plantilla.getNombre());
        return perfil;
    }

    private void asegurarPlantillas() {
        if (plantillas.count() > 0) {
            return;
        }
        Map<String, Set<String>> base = new LinkedHashMap<>();
        base.put("Capturista", Set.of("ALUMNOS_CONSULTAR", "ALUMNOS_CAPTURAR",
                "INSCRIPCION_CONSULTAR", "INSCRIPCION_CAPTURAR"));
        base.put("Docente", Set.of("EVALUACION_CONSULTAR", "EVALUACION_CAPTURAR"));
        base.put("Emisor de documentos", Set.of("DOCUMENTOS_CONSULTAR", "DOCUMENTOS_EMITIR"));
        base.forEach(this::crearPlantilla);
    }

    public record FilaUsuario(Usuario usuario, Long perfilId, String perfilNombre) {
    }

    public Set<String> codigosDe(Long usuarioId) {
        Set<String> codigos = new HashSet<>();
        for (UsuarioPerfil asignacion : asignaciones.findByUsuarioId(usuarioId)) {
            for (PerfilPermiso enlace : enlaces.findByPerfilId(asignacion.getPerfilId())) {
                permisos.findById(enlace.getPermisoId()).ifPresent(permiso -> codigos.add(permiso.getCodigo()));
            }
        }
        return codigos;
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
