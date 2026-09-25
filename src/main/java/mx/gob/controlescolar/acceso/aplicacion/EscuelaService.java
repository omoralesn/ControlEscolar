package mx.gob.controlescolar.acceso.aplicacion;

import mx.gob.controlescolar.acceso.dominio.ClaveCentroTrabajo;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.InstitucionModulo;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.dominio.Perfil;
import mx.gob.controlescolar.acceso.dominio.PermisoCatalogo;
import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.persistencia.InstitucionModuloRepositorio;
import mx.gob.controlescolar.acceso.persistencia.InstitucionRepositorio;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.comun.dominio.Localidad;
import mx.gob.controlescolar.comun.persistencia.LocalidadRepositorio;
import mx.gob.controlescolar.personas.dominio.Domicilio;
import mx.gob.controlescolar.personas.persistencia.DomicilioRepositorio;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EscuelaService {

    private final InstitucionRepositorio instituciones;
    private final InstitucionModuloRepositorio modulos;
    private final UsuarioRepositorio usuarios;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoria;
    private final PerfilService perfiles;
    private final CentroTrabajoService centros;
    private final DomicilioRepositorio domicilios;
    private final LocalidadRepositorio localidades;

    @Transactional
    public Institucion alta(String nombre, String claveCct, boolean particular, Set<Modulo> habilitados,
                            String loginEscolar, String claveEscolar) {
        return alta(nombre, claveCct, particular, habilitados, loginEscolar, claveEscolar, null, null);
    }

    @Transactional
    public Institucion alta(String nombre, String claveCct, boolean particular, Set<Modulo> habilitados,
                            String loginEscolar, String claveEscolar, DatosDomicilio domicilio, DatosContacto contacto) {
        Institucion institucion = instituciones.save(new Institucion(nombre, claveCct, particular));
        Set<Modulo> activos = conUsuarios(habilitados);
        for (Modulo modulo : activos) {
            modulos.save(new InstitucionModulo(institucion, modulo));
        }
        if (domicilio != null) {
            institucion.asignarDomicilio(construirDomicilio(domicilio));
        }
        if (contacto != null) {
            institucion.definirContacto(contacto.nombre(), contacto.telefono(), contacto.celular(), contacto.correo());
        }
        Perfil adminPerfil = perfiles.prepararAdministrador(institucion, PermisoCatalogo.de(activos));
        if (loginEscolar != null && !loginEscolar.isBlank()) {
            if (claveEscolar == null || claveEscolar.isBlank()) {
                throw new NegocioException("La contraseña del usuario es obligatoria");
            }
            Usuario administrador = usuarios.save(new Usuario(institucion, loginEscolar,
                    passwordEncoder.encode(claveEscolar), "Administrador escolar"));
            perfiles.asignarPerfil(administrador.getId(), adminPerfil.getId());
            auditoria.registrar(institucion.getId(), "ALTA_ESCUELA", nombre, loginEscolar, "Administrador escolar");
        } else {
            auditoria.registrar(institucion.getId(), "ALTA_ESCUELA", nombre);
        }
        return institucion;
    }

    /**
     * Alta estándar del super: la CCT (mayúsculas) define entidad, nivel, clasificador, sostenimiento y progresivo.
     * Los usuarios se dan de alta aparte. Solo si el clasificador es D se declara FEDERAL o DESCONCENTRADA.
     */
    @Transactional
    public Institucion altaPorCct(String nombre, String claveCct, String sostenimientoDeclarado,
                                  Set<Modulo> habilitados) {
        return altaPorCct(nombre, claveCct, sostenimientoDeclarado, habilitados, null, null);
    }

    @Transactional
    public Institucion altaPorCct(String nombre, String claveCct, String sostenimientoDeclarado,
                                  Set<Modulo> habilitados, String loginEscolar, String claveEscolar) {
        if (claveCct == null || claveCct.isBlank()) {
            throw new NegocioException("La CCT es obligatoria para toda escuela");
        }
        String cct = claveCct.trim().toUpperCase();
        ClaveCentroTrabajo clave = ClaveCentroTrabajo.analizar(cct, sostenimientoDeclarado);
        Institucion institucion = alta(nombre, cct, esParticular(clave.sostenimiento()), habilitados,
                loginEscolar, claveEscolar, null, null);
        centros.registrar(institucion.getId(), cct, clave.sostenimiento(), nombre);
        return institucion;
    }

    @Transactional
    public Institucion altaNivel(String nombre, String claveCct, String nivel, String sostenimiento,
                                 Set<Modulo> habilitados, String loginEscolar, String claveEscolar,
                                 DatosDomicilio domicilio, DatosContacto contacto) {
        if (claveCct == null || claveCct.isBlank()) {
            throw new NegocioException("La CCT es obligatoria para toda escuela");
        }
        String cct = claveCct.trim().toUpperCase();
        ClaveCentroTrabajo clave = ClaveCentroTrabajo.analizar(cct, sostenimiento);
        if (nivel != null && !nivel.isBlank() && !nivel.equals(clave.nivel())) {
            throw new NegocioException("La CCT no corresponde al nivel de la escuela");
        }
        Institucion institucion = alta(nombre, cct, esParticular(clave.sostenimiento()), habilitados,
                loginEscolar, claveEscolar, domicilio, contacto);
        centros.registrar(institucion.getId(), cct, clave.sostenimiento(), nombre);
        return institucion;
    }

    @Transactional
    public void actualizarDomicilio(Long institucionId, DatosDomicilio datos) {
        Institucion institucion = obtener(institucionId);
        Domicilio domicilio = institucion.getDomicilio() == null
                ? construirDomicilio(datos)
                : aplicar(institucion.getDomicilio(), datos);
        institucion.asignarDomicilio(domicilios.save(domicilio));
        auditoria.registrar(institucionId, "DATOS_ESCUELA", "Domicilio actualizado");
    }

    @Transactional
    public void actualizarContacto(Long institucionId, DatosContacto contacto) {
        obtener(institucionId).definirContacto(contacto.nombre(), contacto.telefono(), contacto.celular(),
                contacto.correo());
        auditoria.registrar(institucionId, "DATOS_ESCUELA", "Contacto actualizado");
    }

    @Transactional
    public void definirModulos(Long institucionId, Set<Modulo> habilitados) {
        Institucion institucion = obtener(institucionId);
        Set<Modulo> activos = conUsuarios(habilitados);
        modulos.deleteByInstitucionId(institucionId);
        for (Modulo modulo : activos) {
            modulos.save(new InstitucionModulo(institucion, modulo));
        }
        perfiles.retirarFueraDeModulos(institucionId);
        auditoria.registrar(institucionId, "MODULOS_ESCUELA", activos.toString());
    }

    @Transactional
    public void suspender(Long institucionId) {
        Institucion institucion = obtener(institucionId);
        institucion.suspender();
        auditoria.registrar(institucionId, "SUSPENSION", institucion.getNombre());
    }

    @Transactional
    public void activar(Long institucionId) {
        Institucion institucion = obtener(institucionId);
        institucion.activar();
        auditoria.registrar(institucionId, "ACTIVACION", institucion.getNombre());
    }

    public Institucion obtener(Long institucionId) {
        return instituciones.findById(institucionId)
                .orElseThrow(() -> new NegocioException("La escuela no existe"));
    }

    public List<Institucion> listar() {
        return instituciones.findAll();
    }

    public List<Localidad> localidadesPorCp(String codigoPostal) {
        if (codigoPostal == null || codigoPostal.isBlank()) {
            return List.of();
        }
        return localidades.findByCodigoPostalOrderByNombreAsc(codigoPostal.trim());
    }

    public static boolean esParticular(String sostenimiento) {
        return sostenimiento != null && "PARTICULAR".equalsIgnoreCase(sostenimiento.trim());
    }

    private static Set<Modulo> conUsuarios(Set<Modulo> habilitados) {
        EnumSet<Modulo> activos = habilitados == null || habilitados.isEmpty()
                ? EnumSet.noneOf(Modulo.class)
                : EnumSet.copyOf(habilitados);
        activos.add(Modulo.USUARIOS);
        return activos;
    }

    private Domicilio construirDomicilio(DatosDomicilio datos) {
        return domicilios.save(aplicar(new Domicilio(null, null, null, null, null, null, null), datos));
    }

    private Domicilio aplicar(Domicilio domicilio, DatosDomicilio datos) {
        Localidad localidad = datos.localidadId() == null ? null : localidades.findById(datos.localidadId())
                .orElseThrow(() -> new NegocioException("La localidad no existe"));
        if (localidad != null && datos.codigoPostal() != null && !datos.codigoPostal().isBlank()
                && localidad.getCodigoPostal() != null
                && !localidad.getCodigoPostal().equals(datos.codigoPostal().trim())) {
            throw new NegocioException("La localidad no corresponde al código postal");
        }
        domicilio.setCalle(datos.calle());
        domicilio.setNumeroExterior(datos.numeroExterior());
        domicilio.setNumeroInterior(datos.numeroInterior());
        domicilio.setColonia(datos.colonia());
        domicilio.setCodigoPostal(datos.codigoPostal());
        domicilio.setReferencia(datos.referencia());
        domicilio.setLocalidad(localidad);
        if (localidad != null) {
            domicilio.setMunicipio(localidad.getMunicipio());
            domicilio.setEstado(localidad.getMunicipio().getEstado());
            if (domicilio.getCodigoPostal() == null || domicilio.getCodigoPostal().isBlank()) {
                domicilio.setCodigoPostal(localidad.getCodigoPostal());
            }
        }
        return domicilio;
    }

    public record DatosDomicilio(String calle, String numeroExterior, String numeroInterior, String colonia,
                                 String codigoPostal, String referencia, Long localidadId) {
    }

    public record DatosContacto(String nombre, String telefono, String celular, String correo) {
    }
}
