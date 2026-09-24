package mx.gob.controlescolar.academico.aplicacion;

import mx.gob.controlescolar.academico.dominio.AsignaturaPlan;
import mx.gob.controlescolar.academico.dominio.CatalogoPlanesSep;
import mx.gob.controlescolar.academico.dominio.EsquemaNivel;
import mx.gob.controlescolar.academico.dominio.EstadoVersion;
import mx.gob.controlescolar.academico.dominio.MomentoEvaluacion;
import mx.gob.controlescolar.academico.dominio.PeriodoPlan;
import mx.gob.controlescolar.academico.dominio.PlanRector;
import mx.gob.controlescolar.academico.dominio.PlanVersion;
import mx.gob.controlescolar.academico.dominio.TipoMomento;
import mx.gob.controlescolar.academico.persistencia.AnexoRepositorio;
import mx.gob.controlescolar.academico.persistencia.AsignaturaPlanRepositorio;
import mx.gob.controlescolar.academico.persistencia.MomentoRepositorio;
import mx.gob.controlescolar.academico.persistencia.PeriodoPlanRepositorio;
import mx.gob.controlescolar.academico.persistencia.PlanRectorRepositorio;
import mx.gob.controlescolar.academico.persistencia.PlanVersionRepositorio;
import mx.gob.controlescolar.academico.persistencia.ProgramaRepositorio;
import mx.gob.controlescolar.academico.persistencia.VentanaRepositorio;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.evaluacion.persistencia.CalificacionRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRectorRepositorio planes;
    private final PlanVersionRepositorio versiones;
    private final MomentoRepositorio momentos;
    private final PeriodoPlanRepositorio periodos;
    private final AsignaturaPlanRepositorio asignaturas;
    private final ProgramaRepositorio programas;
    private final AnexoRepositorio anexos;
    private final CalificacionRepositorio calificaciones;
    private final VentanaRepositorio ventanas;
    private final ModuloGuardia modulos;

    @Transactional
    public PlanVersion publicarConMomentos(String nivel, String nombre, String tipoPeriodo, BigDecimal minimo,
                                           BigDecimal maximo, BigDecimal aprobatoria, boolean cualitativa,
                                           boolean reinscribeSinPromedio, Integer maxReprobadas,
                                           List<DefinicionMomento> definiciones) {
        PlanRector rector = planes.save(new PlanRector(nivel, nombre, tipoPeriodo));
        PlanVersion version = versiones.save(new PlanVersion(rector, 1, EstadoVersion.VIGENTE, minimo, maximo,
                aprobatoria, cualitativa, reinscribeSinPromedio, maxReprobadas));
        fijarEsquema(version);
        int orden = 1;
        for (DefinicionMomento definicion : definiciones) {
            momentos.save(new MomentoEvaluacion(version, orden++, definicion.nombre(), definicion.tipo(),
                    definicion.tipo() == TipoMomento.PROMEDIO || definicion.tipo() == TipoMomento.ORDINARIO));
        }
        return version;
    }

    @Transactional
    public void cambiarAprobatoria(Long planVersionId, BigDecimal aprobatoria) {
        if (calificaciones.existsByPlanVersionId(planVersionId)) {
            throw new NegocioException("La versión ya tiene calificaciones. Publique otra versión.");
        }
        PlanVersion version = versiones.findById(planVersionId)
                .orElseThrow(() -> new NegocioException("La versión no existe"));
        if (version.getEstado() == EstadoVersion.CERRADO) {
            throw new NegocioException("La versión cerrada no se edita");
        }
        if (!version.isEsquemaEditable()) {
            throw new NegocioException("La básica no cambia la calificación aprobatoria");
        }
        version.setAprobatoria(aprobatoria);
    }

    @Transactional
    public void configurarEsquema(Long planVersionId, BigDecimal aprobatoria, int ordinarios,
                                  boolean extraordinarios, boolean segundoCurso) {
        if (calificaciones.existsByPlanVersionId(planVersionId)) {
            throw new NegocioException("La versión ya tiene calificaciones. Publique otra versión.");
        }
        PlanVersion version = obtener(planVersionId);
        if (!version.isEsquemaEditable()) {
            throw new NegocioException("La básica no cambia el esquema de evaluación");
        }
        if (ordinarios < 1) {
            throw new NegocioException("El esquema necesita al menos una evaluación ordinaria");
        }
        version.setAprobatoria(aprobatoria);
        for (MomentoEvaluacion momento : momentos.findByPlanVersionIdOrderByOrden(planVersionId)) {
            ventanas.deleteByMomentoId(momento.getId());
        }
        momentos.deleteByPlanVersionId(planVersionId);
        int orden = 1;
        for (int i = 1; i <= ordinarios; i++) {
            momentos.save(new MomentoEvaluacion(version, orden++, "Evaluación " + i, TipoMomento.ORDINARIO, true));
        }
        if (extraordinarios) {
            momentos.save(new MomentoEvaluacion(version, orden++, "Extraordinario", TipoMomento.EXTRAORDINARIO, false));
        }
        if (segundoCurso) {
            momentos.save(new MomentoEvaluacion(version, orden, "Segundo curso", TipoMomento.SEGUNDO_CURSO, false));
        }
    }

    @Transactional
    public PeriodoPlan agregarPeriodo(Long planVersionId, int orden, String nombre) {
        return periodos.save(new PeriodoPlan(obtener(planVersionId), orden, nombre));
    }

    @Transactional
    public AsignaturaPlan agregarAsignatura(Long planVersionId, String clave, String nombre, Integer horas,
                                            Integer creditos, boolean optativa, String modulo, String submodulo) {
        return asignaturas.save(new AsignaturaPlan(obtener(planVersionId), clave, nombre, horas, creditos,
                optativa, modulo, submodulo, false));
    }

    @Transactional
    public PlanVersion publicarPlanGeneral(CatalogoPlanesSep.PlanGeneral general, BigDecimal minimo,
                                           BigDecimal maximo, BigDecimal aprobatoria, boolean cualitativa,
                                           boolean reinscribeSinPromedio, Integer maxReprobadas,
                                           List<DefinicionMomento> definiciones) {
        PlanVersion version = publicarConMomentos(general.nivel(), general.nombre(), general.tipoPeriodo(), minimo,
                maximo, aprobatoria, cualitativa, reinscribeSinPromedio, maxReprobadas, definiciones);
        alinearOficiales(version.getId(), general);
        return version;
    }

    @Transactional
    public void alinearOficiales(Long planVersionId, CatalogoPlanesSep.PlanGeneral general) {
        PlanVersion version = obtener(planVersionId);
        List<AsignaturaPlan> actuales = new ArrayList<>(asignaturas.findByPlanVersionId(planVersionId));
        if (!general.asignaturas().isEmpty()) {
            for (AsignaturaPlan actual : List.copyOf(actuales)) {
                boolean delPlan = general.asignaturas().stream()
                        .anyMatch(oficial -> oficial.clave().equals(actual.getClave()));
                if (!delPlan && !calificaciones.existsByPlanVersionIdAndAsignaturaClave(planVersionId, actual.getClave())) {
                    asignaturas.delete(actual);
                    actuales.remove(actual);
                }
            }
        }
        for (CatalogoPlanesSep.AsignaturaOficial oficial : general.asignaturas()) {
            actuales.stream()
                    .filter(asignatura -> asignatura.getClave().equals(oficial.clave()))
                    .findFirst()
                    .ifPresentOrElse(
                            asignatura -> asignatura.reconocerOficial(oficial.nombre(), oficial.campo(), oficial.fase()),
                            () -> asignaturas.save(new AsignaturaPlan(version, oficial.clave(), oficial.nombre(), null,
                                    null, false, oficial.campo(), oficial.fase(), true)));
        }
    }

    @Transactional
    public void cerrarVersion(Long institucionId, Long planVersionId) {
        modulos.exigir(institucionId, Modulo.PLANES);
        PlanVersion version = obtener(planVersionId);
        version.cerrar();
        programas.findByPlanVersionId(planVersionId).forEach(programa ->
                anexos.findByProgramaIdAndEstado(programa.getId(), EstadoVersion.VIGENTE)
                        .ifPresent(anexo -> anexo.cerrar()));
    }

    @Transactional
    public void cerrarPeriodo(Long institucionId, Long planVersionId) {
        cerrarVersion(institucionId, planVersionId);
    }

    @Transactional
    public PlanVersion nuevaVersion(Long planVersionId, BigDecimal aprobatoria) {
        PlanVersion anterior = versiones.findById(planVersionId)
                .orElseThrow(() -> new NegocioException("La versión no existe"));
        anterior.cerrar();
        PlanVersion nueva = versiones.save(new PlanVersion(anterior.getPlanRector(), anterior.getNumero() + 1,
                EstadoVersion.VIGENTE, null, null, aprobatoria, anterior.isCualitativa(), false, null));
        nueva.copiarReglas(anterior);
        nueva.setAprobatoria(aprobatoria);
        int orden = 1;
        for (MomentoEvaluacion momento : momentos.findByPlanVersionIdOrderByOrden(anterior.getId())) {
            momentos.save(new MomentoEvaluacion(nueva, orden++, momento.getNombre(), momento.getTipo(),
                    momento.getTipo() == TipoMomento.PROMEDIO));
        }
        for (AsignaturaPlan asignatura : asignaturas.findByPlanVersionId(anterior.getId())) {
            asignaturas.save(new AsignaturaPlan(nueva, asignatura.getClave(), asignatura.getNombre(),
                    asignatura.getHoras(), asignatura.getCreditos(), asignatura.isOptativa(), asignatura.getModulo(),
                    asignatura.getSubmodulo(), asignatura.isOficial()));
        }
        return nueva;
    }

    public Optional<PlanVersion> buscarVigente(String nivel, String nombre) {
        return versiones.findByEstado(EstadoVersion.VIGENTE).stream()
                .filter(version -> version.getPlanRector().getNivel().equals(nivel)
                        && version.getPlanRector().getNombre().equals(nombre))
                .findFirst();
    }

    public List<MomentoEvaluacion> momentosDe(Long planVersionId) {
        return momentos.findByPlanVersionIdOrderByOrden(planVersionId);
    }

    public List<PeriodoPlan> periodosDe(Long planVersionId) {
        return periodos.findByPlanVersionIdOrderByOrden(planVersionId);
    }

    public List<AsignaturaPlan> asignaturasDe(Long planVersionId) {
        return asignaturas.findByPlanVersionId(planVersionId);
    }

    public List<PlanVersion> vigentes() {
        return versiones.findByEstado(EstadoVersion.VIGENTE);
    }

    public PlanVersion obtener(Long id) {
        return versiones.findById(id).orElseThrow(() -> new NegocioException("La versión no existe"));
    }

    public List<PlanRector> listarRectores() {
        return planes.findAll();
    }

    @Transactional
    public void definirUmbralCreditos(Long planVersionId, Integer umbral) {
        obtener(planVersionId).definirUmbralCreditos(umbral);
    }

    public void exigirAprobada(Long planVersionId, BigDecimal valor) {
        PlanVersion version = obtener(planVersionId);
        if (version.isCualitativa() || version.getAprobatoria() == null || valor == null) {
            return;
        }
        if (valor.compareTo(version.getAprobatoria()) < 0) {
            throw new NegocioException("No alcanza la calificación aprobatoria");
        }
    }

    public record DefinicionMomento(String nombre, TipoMomento tipo) {
    }

    private void fijarEsquema(PlanVersion version) {
        if (!EsquemaNivel.basica(version.getPlanRector().getNivel())) {
            return;
        }
        version.bloquearEsquema();
        if (!version.isCualitativa()) {
            version.setAprobatoria(new BigDecimal("6"));
        }
    }
}
