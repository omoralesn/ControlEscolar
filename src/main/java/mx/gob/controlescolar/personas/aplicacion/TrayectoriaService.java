package mx.gob.controlescolar.personas.aplicacion;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Generated;
import mx.gob.controlescolar.academico.dominio.AsignaturaPlan;
import mx.gob.controlescolar.academico.dominio.PlanVersion;
import mx.gob.controlescolar.academico.persistencia.AsignaturaPlanRepositorio;
import mx.gob.controlescolar.academico.persistencia.PeriodoPlanRepositorio;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.persistencia.InstitucionRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.evaluacion.dominio.Calificacion;
import mx.gob.controlescolar.evaluacion.persistencia.CalificacionRepositorio;
import mx.gob.controlescolar.inscripcion.dominio.Grupo;
import mx.gob.controlescolar.inscripcion.persistencia.GrupoRepositorio;
import mx.gob.controlescolar.personas.dominio.Alumno;
import mx.gob.controlescolar.personas.dominio.Generacion;
import mx.gob.controlescolar.personas.dominio.Inscripcion;
import mx.gob.controlescolar.personas.dominio.MovimientoAlumno;
import mx.gob.controlescolar.personas.persistencia.AlumnoRepositorio;
import mx.gob.controlescolar.personas.persistencia.GeneracionRepositorio;
import mx.gob.controlescolar.personas.persistencia.InscripcionRepositorio;
import mx.gob.controlescolar.personas.persistencia.MovimientoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrayectoriaService {
    private final GeneracionRepositorio generaciones;
    private final InstitucionRepositorio instituciones;
    private final AlumnoRepositorio alumnos;
    private final GrupoRepositorio grupos;
    private final InscripcionRepositorio inscripciones;
    private final MovimientoRepositorio movimientos;
    private final CalificacionRepositorio calificaciones;
    private final AsignaturaPlanRepositorio asignaturas;
    private final PeriodoPlanRepositorio periodos;
    private final ModuloGuardia modulos;

    @Transactional
    public Generacion crearGeneracion(Long institucionId, String nombre, int anioInicio) {
        this.modulos.exigir(institucionId, Modulo.ALUMNOS);
        Institucion institucion = (Institucion)this.instituciones.findById(institucionId).orElseThrow();
        return (Generacion)this.generaciones.save(new Generacion(institucion, nombre, anioInicio));
    }

    @Transactional
    public void asignarGeneracion(Long institucionId, Long alumnoId, Long grupoId, Long generacionId) {
        this.modulos.exigir(institucionId, Modulo.ALUMNOS);
        Generacion generacion = (Generacion)this.generaciones.findById(generacionId).orElseThrow();
        if (!generacion.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("La generaci\u00f3n no pertenece a la escuela");
        }
        Alumno alumno = (Alumno)this.alumnos.findById(alumnoId).orElseThrow();
        if (!alumno.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El alumno no pertenece a la escuela");
        }
        alumno.asignarGeneracion(generacion);
        if (grupoId != null) {
            Grupo grupo = (Grupo)this.grupos.findById(grupoId).orElseThrow();
            grupo.asignarGeneracion(generacion);
        }
    }

    public int creditosAcumulados(Long alumnoId, Long planVersionId) {
        List<AsignaturaPlan> plan = this.asignaturas.findByPlanVersionId(planVersionId);
        Set<String> aprobadas = this.clavesAprobadas(alumnoId, planVersionId, plan);
        return plan.stream().filter(asignatura -> aprobadas.contains(asignatura.getClave())).mapToInt(asignatura -> asignatura.getCreditos() == null ? 0 : asignatura.getCreditos()).sum();
    }

    public boolean elegiblePorCreditos(Long alumnoId, PlanVersion version) {
        if (version.getUmbralCreditos() == null) {
            return false;
        }
        return this.creditosAcumulados(alumnoId, version.getId()) >= version.getUmbralCreditos();
    }

    @Transactional
    public void promover(Long institucionId, Long alumnoId, Long grupoDestinoId) {
        Grupo destino;
        this.modulos.exigir(institucionId, Modulo.ALUMNOS);
        Inscripcion inscripcion = this.inscripciones.findByAlumnoIdAndHistoricaFalse(alumnoId).orElseThrow(() -> new NegocioException("El alumno no tiene inscripci\u00f3n activa"));
        PlanVersion version = inscripcion.getPlanVersion();
        if (!version.isReinscribeSinPromedio() && !version.isCualitativa()) {
            int reprobadas = this.reprobadas(alumnoId, version);
            if (version.getMaxMateriasReprobadas() != null && reprobadas > version.getMaxMateriasReprobadas()) {
                throw new NegocioException("Supera el tope de materias reprobadas del plan");
            }
        }
        if (!(destino = (Grupo)this.grupos.findById(grupoDestinoId).orElseThrow()).getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El grupo no pertenece a la escuela");
        }
        inscripcion.cambiarGrupo(destino);
        this.movimientos.save(new MovimientoAlumno(institucionId, alumnoId, "PROMOCION", destino.getNombre()));
    }

    @Transactional
    public void egresar(Long institucionId, Long alumnoId) {
        this.modulos.exigir(institucionId, Modulo.ALUMNOS);
        Inscripcion inscripcion = this.inscripciones.findByAlumnoIdAndHistoricaFalse(alumnoId).orElseThrow(() -> new NegocioException("El alumno no tiene inscripci\u00f3n activa"));
        PlanVersion version = inscripcion.getPlanVersion();
        int ultimo = this.periodos.findByPlanVersionIdOrderByOrden(version.getId()).stream().mapToInt(periodo -> periodo.getOrden()).max().orElse(inscripcion.getGrupo().getPeriodoOrden());
        if (inscripcion.getGrupo().getPeriodoOrden() < ultimo) {
            throw new NegocioException("A\u00fan no cursa el \u00faltimo periodo del plan");
        }
        if (version.getUmbralCreditos() != null && !this.elegiblePorCreditos(alumnoId, version)) {
            throw new NegocioException("No alcanza el umbral de cr\u00e9ditos");
        }
        if (!version.isReinscribeSinPromedio() && !version.isCualitativa() && version.getMaxMateriasReprobadas() != null && this.reprobadas(alumnoId, version) > version.getMaxMateriasReprobadas()) {
            throw new NegocioException("No puede egresar con materias reprobadas por encima del tope");
        }
        inscripcion.getAlumno().egresar();
        inscripcion.marcarHistorica();
        this.movimientos.save(new MovimientoAlumno(institucionId, alumnoId, "EGRESO", "Egresado"));
    }

    public List<Generacion> generaciones(Long institucionId) {
        return this.generaciones.findByInstitucionId(institucionId);
    }

    private int reprobadas(Long alumnoId, PlanVersion version) {
        List<AsignaturaPlan> plan = this.asignaturas.findByPlanVersionId(version.getId());
        HashSet<String> conCalificacion = new HashSet<String>();
        Set<String> aprobadas = this.clavesAprobadas(alumnoId, version.getId(), plan);
        for (Calificacion calificacion : this.calificaciones.findByAlumnoIdAndPlanVersionId(alumnoId, version.getId())) {
            if (calificacion.getValor() == null) continue;
            conCalificacion.add(calificacion.getAsignaturaClave());
        }
        conCalificacion.removeAll(aprobadas);
        return conCalificacion.size();
    }

    private Set<String> clavesAprobadas(Long alumnoId, Long planVersionId, List<AsignaturaPlan> plan) {
        BigDecimal umbral = plan.isEmpty() ? null : plan.get(0).getPlanVersion().getAprobatoria();
        HashSet<String> aprobadas = new HashSet<String>();
        if (umbral == null) {
            return aprobadas;
        }
        for (Calificacion calificacion : this.calificaciones.findByAlumnoIdAndPlanVersionId(alumnoId, planVersionId)) {
            if (calificacion.getValor() == null || calificacion.getValor().compareTo(umbral) < 0) continue;
            aprobadas.add(calificacion.getAsignaturaClave());
        }
        return aprobadas;
    }

    @Generated
    public TrayectoriaService(GeneracionRepositorio generaciones, InstitucionRepositorio instituciones, AlumnoRepositorio alumnos, GrupoRepositorio grupos, InscripcionRepositorio inscripciones, MovimientoRepositorio movimientos, CalificacionRepositorio calificaciones, AsignaturaPlanRepositorio asignaturas, PeriodoPlanRepositorio periodos, ModuloGuardia modulos) {
        this.generaciones = generaciones;
        this.instituciones = instituciones;
        this.alumnos = alumnos;
        this.grupos = grupos;
        this.inscripciones = inscripciones;
        this.movimientos = movimientos;
        this.calificaciones = calificaciones;
        this.asignaturas = asignaturas;
        this.periodos = periodos;
        this.modulos = modulos;
    }
}
