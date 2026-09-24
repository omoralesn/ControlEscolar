package mx.gob.controlescolar.academico.aplicacion;

import mx.gob.controlescolar.academico.dominio.CalendarioEscolar;
import mx.gob.controlescolar.academico.dominio.CicloEscolar;
import mx.gob.controlescolar.academico.dominio.EsquemaNivel;
import mx.gob.controlescolar.academico.dominio.EventoCalendario;
import mx.gob.controlescolar.academico.dominio.MomentoEvaluacion;
import mx.gob.controlescolar.academico.dominio.PeriodoCiclo;
import mx.gob.controlescolar.academico.dominio.PeriodoPlan;
import mx.gob.controlescolar.academico.dominio.VentanaCaptura;
import mx.gob.controlescolar.academico.persistencia.CalendarioRepositorio;
import mx.gob.controlescolar.academico.persistencia.CicloRepositorio;
import mx.gob.controlescolar.academico.persistencia.EventoCalendarioRepositorio;
import mx.gob.controlescolar.academico.persistencia.MomentoRepositorio;
import mx.gob.controlescolar.academico.persistencia.PeriodoCicloRepositorio;
import mx.gob.controlescolar.academico.persistencia.PeriodoPlanRepositorio;
import mx.gob.controlescolar.academico.persistencia.PlanVersionRepositorio;
import mx.gob.controlescolar.academico.persistencia.VentanaRepositorio;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.persistencia.InstitucionRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarioService {

    private final CicloRepositorio ciclos;
    private final CalendarioRepositorio calendarios;
    private final EventoCalendarioRepositorio eventos;
    private final PeriodoCicloRepositorio periodosCiclo;
    private final PeriodoPlanRepositorio periodosPlan;
    private final VentanaRepositorio ventanas;
    private final MomentoRepositorio momentos;
    private final PlanVersionRepositorio versiones;
    private final InstitucionRepositorio instituciones;

    @Transactional
    public CicloEscolar asegurarCiclo(String nombre, LocalDate inicio, LocalDate fin) {
        return ciclos.findByNombre(nombre).orElseGet(() -> ciclos.save(new CicloEscolar(nombre, inicio, fin)));
    }

    @Transactional
    public CalendarioEscolar publicarOficial(Long cicloId, String nivel, Integer diasEfectivos) {
        if (!EsquemaNivel.basica(nivel)) {
            throw new NegocioException("El calendario oficial de plataforma es solo de educación básica");
        }
        return oficial(cicloId, nivel).orElseGet(() -> calendarios.save(
                new CalendarioEscolar(ciclos.findById(cicloId).orElseThrow(), nivel, null, diasEfectivos)));
    }

    @Transactional
    public void sembrarBasica2025() {
        CicloEscolar ciclo = asegurarCiclo("2025-2026", LocalDate.of(2025, 9, 1), LocalDate.of(2026, 7, 15));
        for (String nivel : List.of("PREESCOLAR", "PRIMARIA", "SECUNDARIA")) {
            CalendarioEscolar calendario = publicarOficial(ciclo.getId(), nivel, 185);
            if (eventos.findByCalendarioId(calendario.getId()).isEmpty()) {
                eventos.save(new EventoCalendario(calendario, "INICIO_CURSOS", "Inicio de cursos",
                        LocalDate.of(2025, 9, 1), LocalDate.of(2025, 9, 1)));
                eventos.save(new EventoCalendario(calendario, "VACACIONES", "Vacaciones de invierno",
                        LocalDate.of(2025, 12, 22), LocalDate.of(2026, 1, 6)));
                eventos.save(new EventoCalendario(calendario, "SUSPENSION", "Suspensión de labores",
                        LocalDate.of(2025, 11, 17), LocalDate.of(2025, 11, 17)));
                eventos.save(new EventoCalendario(calendario, "FIN_CURSOS", "Fin de cursos",
                        LocalDate.of(2026, 7, 15), LocalDate.of(2026, 7, 15)));
            }
        }
    }

    @Transactional
    public CalendarioEscolar crearDeEscuela(Long institucionId, Long cicloId, String nivel, String tipoPeriodo,
                                            Integer diasEfectivos) {
        if (EsquemaNivel.basica(nivel)) {
            throw new NegocioException("La básica adopta el calendario oficial; no publica otro");
        }
        Institucion institucion = instituciones.findById(institucionId)
                .orElseThrow(() -> new NegocioException("La escuela no existe"));
        if ("SUPERIOR".equals(nivel)) {
            boolean alineado = versiones.findAll().stream()
                    .anyMatch(version -> "SUPERIOR".equals(version.getPlanRector().getNivel())
                            && tipoPeriodo != null
                            && tipoPeriodo.equalsIgnoreCase(version.getPlanRector().getTipoPeriodo()));
            if (!alineado) {
                throw new NegocioException("El tipo de periodo debe coincidir con el plan de la escuela");
            }
        }
        return calendarios.findByCicloIdAndNivelAndInstitucionId(cicloId, nivel, institucionId)
                .orElseGet(() -> calendarios.save(new CalendarioEscolar(
                        ciclos.findById(cicloId).orElseThrow(), nivel, institucion, diasEfectivos)));
    }

    @Transactional
    public EventoCalendario agregarEvento(Long calendarioId, String tipo, String nombre, LocalDate inicio, LocalDate fin) {
        CalendarioEscolar calendario = calendarios.findById(calendarioId).orElseThrow();
        if (calendario.oficial()) {
            throw new NegocioException("El calendario oficial de básica no se edita en la escuela");
        }
        return eventos.save(new EventoCalendario(calendario, tipo, nombre, inicio, fin));
    }

    @Transactional
    public PeriodoCiclo abrirPeriodo(Long calendarioId, Long periodoPlanId, LocalDate inicio, LocalDate fin) {
        CalendarioEscolar calendario = calendarios.findById(calendarioId).orElseThrow();
        PeriodoPlan periodo = periodosPlan.findById(periodoPlanId).orElseThrow();
        return periodosCiclo.findByCalendarioIdAndOrden(calendarioId, periodo.getOrden())
                .orElseGet(() -> periodosCiclo.save(new PeriodoCiclo(calendario, periodo, inicio, fin)));
    }

    @Transactional
    public VentanaCaptura configurarVentana(Long periodoCicloId, Long momentoId, LocalDate capturaDesde,
                                            LocalDate capturaHasta, LocalDate publicacionDesde) {
        PeriodoCiclo periodo = periodosCiclo.findById(periodoCicloId).orElseThrow();
        if (!periodo.abierto()) {
            throw new NegocioException("El periodo ya está cerrado");
        }
        MomentoEvaluacion momento = momentos.findById(momentoId)
                .orElseThrow(() -> new NegocioException("El momento no existe"));
        if (!momento.getPlanVersion().getId().equals(periodo.getPeriodoPlan().getPlanVersion().getId())) {
            throw new NegocioException("El momento no pertenece al periodo");
        }
        return ventanas.save(new VentanaCaptura(periodo, momento, capturaDesde, capturaHasta, publicacionDesde));
    }

    @Transactional
    public void cerrarPeriodo(Long periodoCicloId) {
        PeriodoCiclo periodo = periodosCiclo.findById(periodoCicloId).orElseThrow();
        periodo.cerrar();
        LocalDate hoy = LocalDate.now();
        for (VentanaCaptura ventana : ventanas.findByPeriodoCicloId(periodoCicloId)) {
            ventana.publicarSiFalta(hoy);
        }
    }

    public void exigirCaptura(Long planVersionId, int periodoOrden, Long momentoId) {
        Optional<VentanaCaptura> ventana = buscar(planVersionId, periodoOrden, momentoId);
        if (ventana.isPresent() && !ventana.get().permiteCaptura(LocalDate.now())) {
            throw new NegocioException("Fuera de la ventana de captura");
        }
    }

    public boolean visible(Long planVersionId, int periodoOrden, Long momentoId) {
        return buscar(planVersionId, periodoOrden, momentoId)
                .map(ventana -> ventana.publicada(LocalDate.now()))
                .orElse(true);
    }

    public Optional<CalendarioEscolar> oficial(Long cicloId, String nivel) {
        return calendarios.findByCicloIdAndNivel(cicloId, nivel).stream()
                .filter(CalendarioEscolar::oficial)
                .findFirst();
    }

    public List<CalendarioEscolar> deLaEscuela(Long institucionId) {
        return calendarios.findByInstitucionId(institucionId);
    }

    public List<CalendarioEscolar> oficiales() {
        return calendarios.findAll().stream().filter(CalendarioEscolar::oficial).toList();
    }

    public List<EventoCalendario> eventosDe(Long calendarioId) {
        return eventos.findByCalendarioId(calendarioId);
    }

    public List<PeriodoCiclo> periodosDe(Long calendarioId) {
        return periodosCiclo.findByCalendarioIdOrderByOrden(calendarioId);
    }

    public List<CicloEscolar> ciclos() {
        return ciclos.findAll();
    }

    private Optional<VentanaCaptura> buscar(Long planVersionId, int periodoOrden, Long momentoId) {
        return ventanas.findByMomentoId(momentoId).stream()
                .filter(ventana -> ventana.getPeriodoCiclo().getOrden() == periodoOrden)
                .filter(ventana -> ventana.getPeriodoCiclo().getPeriodoPlan().getPlanVersion().getId().equals(planVersionId))
                .max(Comparator.comparing(VentanaCaptura::getId));
    }
}
