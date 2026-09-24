package mx.gob.controlescolar.evaluacion.aplicacion;

import mx.gob.controlescolar.academico.aplicacion.CalendarioService;
import mx.gob.controlescolar.academico.dominio.AsignaturaPlan;
import mx.gob.controlescolar.academico.dominio.MomentoEvaluacion;
import mx.gob.controlescolar.academico.dominio.PlanVersion;
import mx.gob.controlescolar.academico.dominio.TipoMomento;
import mx.gob.controlescolar.academico.persistencia.AsignaturaPlanRepositorio;
import mx.gob.controlescolar.academico.persistencia.MomentoRepositorio;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.evaluacion.dominio.Calificacion;
import mx.gob.controlescolar.evaluacion.persistencia.CalificacionRepositorio;
import mx.gob.controlescolar.padres.aplicacion.AvisoService;
import mx.gob.controlescolar.personas.dominio.Inscripcion;
import mx.gob.controlescolar.personas.persistencia.InscripcionRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CalificacionService {

    private final CalificacionRepositorio calificaciones;
    private final MomentoRepositorio momentos;
    private final AsignaturaPlanRepositorio asignaturas;
    private final InscripcionRepositorio inscripciones;
    private final ModuloGuardia modulos;
    private final AvisoService avisos;
    private final CalendarioService calendarios;

    @Transactional
    public Calificacion capturar(Long institucionId, Long alumnoId, String asignatura, int periodoOrden,
                                 Long momentoId, BigDecimal valor, String observacion, String acta,
                                 boolean complementaria) {
        return capturar(institucionId, alumnoId, asignatura, periodoOrden, momentoId, valor, observacion, acta,
                complementaria, null);
    }

    @Transactional
    public Calificacion capturarExtraordinario(Long institucionId, Long alumnoId, String asignatura, int periodoOrden,
                                               BigDecimal valor, int orden, String causa, boolean presentado, String acta) {
        Inscripcion inscripcion = inscripciones.findByAlumnoIdAndHistoricaFalse(alumnoId)
                .orElseThrow(() -> new NegocioException("El alumno no tiene inscripción activa"));
        MomentoEvaluacion extraordinario = momentos.findByPlanVersionIdOrderByOrden(inscripcion.getPlanVersion().getId())
                .stream()
                .filter(momento -> momento.getTipo() == TipoMomento.EXTRAORDINARIO)
                .findFirst()
                .orElseThrow(() -> new NegocioException("El esquema no incluye extraordinarios"));
        if (orden > 1) {
            boolean previo = calificaciones
                    .findByAlumnoIdAndAsignaturaClaveAndPeriodoOrdenAndMomentoIdOrderById(
                            alumnoId, asignatura, periodoOrden, extraordinario.getId())
                    .stream()
                    .anyMatch(anterior -> Objects.equals(anterior.getOrdenExtra(), orden - 1)
                            && Boolean.TRUE.equals(anterior.getPresentado()));
            if (!previo) {
                throw new NegocioException("El extraordinario anterior no se presentó");
            }
        }
        if (!"FALTAS".equals(causa) && !"CALIFICACION".equals(causa)) {
            throw new NegocioException("El extraordinario exige causa de faltas o de calificación");
        }
        Calificacion calificacion = capturar(institucionId, alumnoId, asignatura, periodoOrden, extraordinario.getId(),
                valor, null, acta, false, orden);
        calificacion.setCausa(causa);
        calificacion.setPresentado(presentado);
        calificacion.setOrdenExtra(orden);
        return calificaciones.save(calificacion);
    }

    @Transactional
    public BigDecimal promedioModulo(Long alumnoId, Long planVersionId, String modulo, int periodoOrden) {
        List<AsignaturaPlan> delModulo = asignaturas.findByPlanVersionId(planVersionId).stream()
                .filter(asignatura -> modulo != null && modulo.equals(asignatura.getModulo()))
                .toList();
        if (delModulo.isEmpty()) {
            throw new NegocioException("El plan no tiene ese módulo");
        }
        List<Long> ordinarios = momentos.findByPlanVersionIdOrderByOrden(planVersionId).stream()
                .filter(momento -> momento.getTipo() == TipoMomento.ORDINARIO)
                .map(MomentoEvaluacion::getId)
                .toList();
        List<BigDecimal> valores = calificaciones.findByAlumnoIdAndPlanVersionId(alumnoId, planVersionId).stream()
                .filter(calificacion -> calificacion.getPeriodoOrden() == periodoOrden)
                .filter(calificacion -> ordinarios.contains(calificacion.getMomentoId()))
                .filter(calificacion -> delModulo.stream().anyMatch(asignatura ->
                        asignatura.getClave().equals(calificacion.getAsignaturaClave())))
                .map(Calificacion::getValor)
                .filter(Objects::nonNull)
                .toList();
        if (valores.isEmpty()) {
            throw new NegocioException("El módulo todavía no tiene calificaciones");
        }
        BigDecimal suma = valores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return suma.divide(BigDecimal.valueOf(valores.size()), 1, RoundingMode.HALF_UP);
    }

    private Calificacion capturar(Long institucionId, Long alumnoId, String asignatura, int periodoOrden,
                                 Long momentoId, BigDecimal valor, String observacion, String acta,
                                 boolean complementaria, Integer ordenExtra) {
        modulos.exigir(institucionId, Modulo.EVALUACION);
        Inscripcion inscripcion = inscripciones.findByAlumnoIdAndHistoricaFalse(alumnoId)
                .orElseThrow(() -> new NegocioException("El alumno no tiene inscripción activa"));
        MomentoEvaluacion momento = momentos.findById(momentoId)
                .orElseThrow(() -> new NegocioException("El momento no existe"));
        if (!momento.getPlanVersion().getId().equals(inscripcion.getPlanVersion().getId())) {
            throw new NegocioException("El momento no pertenece al esquema del alumno");
        }
        if (momento.getTipo() == TipoMomento.EXTRAORDINARIO && (acta == null || acta.isBlank())) {
            throw new NegocioException("El extraordinario requiere acta");
        }
        if (momento.getTipo() == TipoMomento.SEGUNDO_CURSO || momento.getTipo() == TipoMomento.EXTRAORDINARIO) {
            boolean incluido = momentos.findByPlanVersionIdOrderByOrden(inscripcion.getPlanVersion().getId()).stream()
                    .anyMatch(propio -> propio.getTipo() == momento.getTipo());
            if (!incluido) {
                throw new NegocioException("El esquema no incluye ese momento");
            }
        }
        calendarios.exigirCaptura(inscripcion.getPlanVersion().getId(), periodoOrden, momentoId);
        PlanVersion plan = inscripcion.getPlanVersion();
        if (!plan.isCualitativa() && valor != null) {
            if (plan.getMinimo() != null && valor.compareTo(plan.getMinimo()) < 0) {
                throw new NegocioException("La calificación está por debajo del mínimo del plan");
            }
            if (plan.getMaximo() != null && valor.compareTo(plan.getMaximo()) > 0) {
                throw new NegocioException("La calificación supera el máximo del plan");
            }
        }
        Calificacion calificacion = calificaciones
                .findByAlumnoIdAndAsignaturaClaveAndPeriodoOrdenAndMomentoIdOrderById(
                        alumnoId, asignatura, periodoOrden, momentoId)
                .stream()
                .filter(existente -> Objects.equals(existente.getOrdenExtra(), ordenExtra))
                .findFirst()
                .orElseGet(() -> {
                    Calificacion nueva = new Calificacion(institucionId, alumnoId, plan.getId(),
                            asignatura, periodoOrden, momentoId, null, null, complementaria);
                    nueva.setOrdenExtra(ordenExtra);
                    return nueva;
                });
        if (plan.isCualitativa()) {
            calificacion.setObservacion(observacion);
            calificacion.setValor(null);
        } else {
            calificacion.setValor(valor);
        }
        calificacion.setActa(acta);
        Calificacion guardada = calificaciones.save(calificacion);
        if (calendarios.visible(plan.getId(), periodoOrden, momentoId)) {
            avisos.publicar(institucionId, alumnoId, "Se publicó una evaluación de " + asignatura);
        }
        return guardada;
    }

    @Transactional
    public Calificacion registrarFaltas(Long institucionId, Long alumnoId, String asignatura, int periodoOrden,
                                        Long momentoId, int faltas) {
        modulos.exigir(institucionId, Modulo.EVALUACION);
        Inscripcion inscripcion = inscripciones.findByAlumnoIdAndHistoricaFalse(alumnoId)
                .orElseThrow(() -> new NegocioException("El alumno no tiene inscripción activa"));
        MomentoEvaluacion momento = momentos.findById(momentoId)
                .orElseThrow(() -> new NegocioException("El momento no existe"));
        if (!momento.getPlanVersion().getId().equals(inscripcion.getPlanVersion().getId())) {
            throw new NegocioException("El momento no pertenece al esquema del alumno");
        }
        Calificacion calificacion = calificaciones
                .findByAlumnoIdAndAsignaturaClaveAndPeriodoOrdenAndMomentoId(alumnoId, asignatura, periodoOrden, momentoId)
                .orElseGet(() -> new Calificacion(institucionId, alumnoId, inscripcion.getPlanVersion().getId(),
                        asignatura, periodoOrden, momentoId, null, null, false));
        calificacion.setFaltas(faltas);
        return calificaciones.save(calificacion);
    }

    public List<Calificacion> delAlumno(Long alumnoId, Long planVersionId) {
        return calificaciones.findByAlumnoIdAndPlanVersionId(alumnoId, planVersionId);
    }
}
