package mx.gob.controlescolar.personas.aplicacion;

import java.util.List;
import lombok.Generated;
import mx.gob.controlescolar.academico.dominio.EstadoVersion;
import mx.gob.controlescolar.academico.dominio.PlanVersion;
import mx.gob.controlescolar.academico.persistencia.AnexoRepositorio;
import mx.gob.controlescolar.academico.persistencia.PlanVersionRepositorio;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.persistencia.InstitucionRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.inscripcion.dominio.Grupo;
import mx.gob.controlescolar.inscripcion.persistencia.GrupoRepositorio;
import mx.gob.controlescolar.personas.dominio.Alumno;
import mx.gob.controlescolar.personas.dominio.Inscripcion;
import mx.gob.controlescolar.personas.dominio.MovimientoAlumno;
import mx.gob.controlescolar.personas.persistencia.AlumnoRepositorio;
import mx.gob.controlescolar.personas.persistencia.InscripcionRepositorio;
import mx.gob.controlescolar.personas.persistencia.MovimientoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlumnoService {
    private final AlumnoRepositorio alumnos;
    private final InscripcionRepositorio inscripciones;
    private final MovimientoRepositorio movimientos;
    private final GrupoRepositorio grupos;
    private final PlanVersionRepositorio versiones;
    private final AnexoRepositorio anexos;
    private final InstitucionRepositorio instituciones;
    private final ModuloGuardia modulos;

    @Transactional
    public Alumno registrar(Long institucionId, Long grupoId, String curp, String nombre, String apellidoPaterno, String apellidoMaterno) {
        this.modulos.exigir(institucionId, Modulo.ALUMNOS);
        Grupo grupo = (Grupo)this.grupos.findById(grupoId).orElseThrow();
        if (!grupo.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El grupo no pertenece a la escuela");
        }
        Alumno alumno = (Alumno)this.alumnos.save(new Alumno((Institucion)this.instituciones.findById(institucionId).orElseThrow(), curp, nombre, apellidoPaterno, apellidoMaterno));
        this.inscripciones.save(new Inscripcion(institucionId, alumno, grupo, grupo.getPrograma().getPlanVersion(), this.anexoVigente(grupo.getPrograma().getId())));
        return alumno;
    }

    @Transactional
    public void modificar(Long institucionId, Long alumnoId, String nombre) {
        this.modulos.exigir(institucionId, Modulo.ALUMNOS);
        Alumno alumno = this.deLaEscuela(institucionId, alumnoId);
        alumno.setNombre(nombre);
    }

    public List<Alumno> porGrupo(Long institucionId, Long grupoId) {
        return this.inscripciones.findByGrupoIdAndHistoricaFalse(grupoId).stream().map(Inscripcion::getAlumno).filter(alumno -> alumno.getInstitucion().getId().equals(institucionId)).toList();
    }

    @Transactional
    public void baja(Long institucionId, Long alumnoId) {
        this.mover(institucionId, alumnoId, "BAJA", "Baja");
        this.deLaEscuela(institucionId, alumnoId).setEstatus("BAJA");
        this.cerrarInscripcion(alumnoId);
    }

    @Transactional
    public void reinscribir(Long institucionId, Long alumnoId) {
        this.mover(institucionId, alumnoId, "REINSCRIPCION", "Reinscripci\u00f3n");
        Alumno alumno = this.deLaEscuela(institucionId, alumnoId);
        alumno.setEstatus("ACTIVO");
        if (this.inscripciones.findByAlumnoIdAndHistoricaFalse(alumnoId).isEmpty()) {
            Inscripcion previa = (Inscripcion)this.inscripciones.findByAlumnoId(alumnoId).stream().reduce((primera, siguiente) -> siguiente).orElseThrow(() -> new NegocioException("El alumno no tiene una inscripci\u00f3n que reabrir"));
            this.inscripciones.save(new Inscripcion(institucionId, alumno, previa.getGrupo(), previa.getPlanVersion(), previa.getAnexoVersionId()));
        }
    }

    @Transactional
    public void trasladar(Long institucionId, Long alumnoId) {
        this.mover(institucionId, alumnoId, "TRASLADO", "Traslado");
        this.deLaEscuela(institucionId, alumnoId).setEstatus("TRASLADO");
        this.cerrarInscripcion(alumnoId);
    }

    @Transactional
    public void repetidor(Long institucionId, Long alumnoId) {
        this.mover(institucionId, alumnoId, "REPETIDOR", "Repetidor");
    }

    @Transactional
    public void cambiarGrupo(Long institucionId, Long alumnoId, Long grupoId) {
        this.modulos.exigir(institucionId, Modulo.ALUMNOS);
        Inscripcion inscripcion = this.inscripcionActiva(alumnoId);
        Grupo grupo = (Grupo)this.grupos.findById(grupoId).orElseThrow();
        if (!grupo.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El grupo no pertenece a la escuela");
        }
        inscripcion.cambiarGrupo(grupo);
        this.movimientos.save(new MovimientoAlumno(institucionId, alumnoId, "CAMBIO_GRUPO", grupo.getNombre()));
    }

    @Transactional
    public Inscripcion cambiarPlan(Long institucionId, Long alumnoId, Long planVersionId) {
        this.modulos.exigir(institucionId, Modulo.ALUMNOS);
        Inscripcion actual = this.inscripcionActiva(alumnoId);
        PlanVersion nueva = (PlanVersion)this.versiones.findById(planVersionId).orElseThrow();
        actual.marcarHistorica();
        this.movimientos.save(new MovimientoAlumno(institucionId, alumnoId, "CAMBIO_PLAN", nueva.getPlanRector().getNombre()));
        return (Inscripcion)this.inscripciones.save(new Inscripcion(institucionId, actual.getAlumno(), actual.getGrupo(), nueva, this.anexoVigente(actual.getGrupo().getPrograma().getId())));
    }

    public List<Inscripcion> historial(Long alumnoId) {
        return this.inscripciones.findByAlumnoId(alumnoId);
    }

    public List<Alumno> listar(Long institucionId) {
        this.modulos.exigir(institucionId, Modulo.ALUMNOS);
        return this.alumnos.findByInstitucionId(institucionId);
    }

    public List<Inscripcion> activas(Long institucionId) {
        this.modulos.exigir(institucionId, Modulo.ALUMNOS);
        return this.inscripciones.findByInstitucionIdAndHistoricaFalse(institucionId);
    }

    private Long anexoVigente(Long programaId) {
        return this.anexos.findByProgramaIdAndEstado(programaId, EstadoVersion.VIGENTE).map(anexo -> anexo.getId()).orElse(null);
    }

    private void mover(Long institucionId, Long alumnoId, String tipo, String detalle) {
        this.modulos.exigir(institucionId, Modulo.ALUMNOS);
        this.deLaEscuela(institucionId, alumnoId);
        this.movimientos.save(new MovimientoAlumno(institucionId, alumnoId, tipo, detalle));
    }

    private Alumno deLaEscuela(Long institucionId, Long alumnoId) {
        Alumno alumno = (Alumno)this.alumnos.findById(alumnoId).orElseThrow();
        if (!alumno.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El alumno no pertenece a la escuela");
        }
        return alumno;
    }

    private Inscripcion inscripcionActiva(Long alumnoId) {
        return this.inscripciones.findByAlumnoIdAndHistoricaFalse(alumnoId).orElseThrow(() -> new NegocioException("El alumno no tiene inscripci\u00f3n activa"));
    }

    private void cerrarInscripcion(Long alumnoId) {
        this.inscripciones.findByAlumnoIdAndHistoricaFalse(alumnoId).ifPresent(Inscripcion::marcarHistorica);
    }

    @Generated
    public AlumnoService(AlumnoRepositorio alumnos, InscripcionRepositorio inscripciones, MovimientoRepositorio movimientos, GrupoRepositorio grupos, PlanVersionRepositorio versiones, AnexoRepositorio anexos, InstitucionRepositorio instituciones, ModuloGuardia modulos) {
        this.alumnos = alumnos;
        this.inscripciones = inscripciones;
        this.movimientos = movimientos;
        this.grupos = grupos;
        this.versiones = versiones;
        this.anexos = anexos;
        this.instituciones = instituciones;
        this.modulos = modulos;
    }
}
