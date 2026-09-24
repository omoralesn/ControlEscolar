package mx.gob.controlescolar.acceso.aplicacion;

import mx.gob.controlescolar.acceso.dominio.ClaveCentroTrabajo;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.InstitucionModulo;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.dominio.PermisoCatalogo;
import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.persistencia.InstitucionModuloRepositorio;
import mx.gob.controlescolar.acceso.persistencia.InstitucionRepositorio;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

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

    @Transactional
    public Institucion alta(String nombre, String claveCct, boolean particular, Set<Modulo> habilitados,
                            String loginEscolar, String claveEscolar) {
        Institucion institucion = instituciones.save(new Institucion(nombre, claveCct, particular));
        for (Modulo modulo : habilitados) {
            modulos.save(new InstitucionModulo(institucion, modulo));
        }
        Usuario administrador = usuarios.save(new Usuario(institucion, loginEscolar,
                passwordEncoder.encode(claveEscolar), "Administrador escolar"));
        perfiles.otorgarAdministrador(institucion, administrador, PermisoCatalogo.de(habilitados));
        auditoria.registrar(institucion.getId(), "ALTA_ESCUELA", nombre);
        return institucion;
    }

    @Transactional
    public Institucion altaNivel(String nombre, String claveCct, boolean particular, String nivel,
                                 String sostenimiento, Set<Modulo> habilitados, String loginEscolar, String claveEscolar) {
        boolean omiteCct = "SUPERIOR".equals(nivel) && particular && (claveCct == null || claveCct.isBlank());
        if ("MEDIA_SUPERIOR".equals(nivel) && (claveCct == null || claveCct.isBlank())) {
            throw new NegocioException("Media superior exige una CCT válida");
        }
        if (!omiteCct && claveCct != null && !claveCct.isBlank()) {
            ClaveCentroTrabajo clave = ClaveCentroTrabajo.analizar(claveCct, sostenimiento);
            if (nivel != null && !nivel.equals(clave.nivel())) {
                throw new NegocioException("La CCT no corresponde al nivel de la escuela");
            }
        }
        Institucion institucion = alta(nombre, claveCct, particular, habilitados, loginEscolar, claveEscolar);
        if (omiteCct) {
            institucion.definirNivel(nivel);
            return institucion;
        }
        if (claveCct != null && !claveCct.isBlank()) {
            centros.registrar(institucion.getId(), claveCct, sostenimiento, nombre);
        } else if (nivel != null) {
            institucion.definirNivel(nivel);
        }
        return institucion;
    }

    @Transactional
    public void definirModulos(Long institucionId, Set<Modulo> habilitados) {
        Institucion institucion = instituciones.findById(institucionId)
                .orElseThrow(() -> new NegocioException("La escuela no existe"));
        modulos.deleteByInstitucionId(institucionId);
        for (Modulo modulo : habilitados) {
            modulos.save(new InstitucionModulo(institucion, modulo));
        }
        perfiles.retirarFueraDeModulos(institucionId);
    }

    @Transactional
    public void suspender(Long institucionId) {
        Institucion institucion = instituciones.findById(institucionId)
                .orElseThrow(() -> new NegocioException("La escuela no existe"));
        institucion.suspender();
        auditoria.registrar(institucionId, "SUSPENSION", institucion.getNombre());
    }

    @Transactional
    public void activar(Long institucionId) {
        Institucion institucion = instituciones.findById(institucionId)
                .orElseThrow(() -> new NegocioException("La escuela no existe"));
        institucion.activar();
        auditoria.registrar(institucionId, "ACTIVACION", institucion.getNombre());
    }

    public List<Institucion> listar() {
        return instituciones.findAll();
    }
}
