package mx.gob.controlescolar;

import mx.gob.controlescolar.academico.aplicacion.CalendarioService;
import mx.gob.controlescolar.academico.aplicacion.PlanService;
import mx.gob.controlescolar.academico.aplicacion.ProgramaService;
import mx.gob.controlescolar.academico.dominio.PlanVersion;
import mx.gob.controlescolar.academico.dominio.TipoMomento;
import mx.gob.controlescolar.acceso.aplicacion.CentroTrabajoService;
import mx.gob.controlescolar.acceso.aplicacion.EscuelaService;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.documentos.aplicacion.DocumentoService;
import mx.gob.controlescolar.evaluacion.aplicacion.CalificacionService;
import mx.gob.controlescolar.inscripcion.aplicacion.GrupoService;
import mx.gob.controlescolar.inscripcion.dominio.Grupo;
import mx.gob.controlescolar.padres.aplicacion.PadreService;
import mx.gob.controlescolar.personas.aplicacion.AccesoTutorService;
import mx.gob.controlescolar.personas.aplicacion.AlumnoService;
import mx.gob.controlescolar.personas.aplicacion.DictamenService;
import mx.gob.controlescolar.personas.aplicacion.TrayectoriaService;
import mx.gob.controlescolar.personas.dominio.Alumno;
import mx.gob.controlescolar.personas.dominio.AlumnoTutor;
import mx.gob.controlescolar.personas.dominio.Tutor;
import mx.gob.controlescolar.personas.persistencia.AlumnoTutorRepositorio;
import mx.gob.controlescolar.personas.persistencia.TutorRepositorio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OperacionEscolarTest {

    @Autowired EscuelaService escuelas;
    @Autowired CentroTrabajoService centros;
    @Autowired PlanService planes;
    @Autowired ProgramaService programas;
    @Autowired CalendarioService calendarios;
    @Autowired GrupoService grupos;
    @Autowired AlumnoService alumnos;
    @Autowired CalificacionService calificaciones;
    @Autowired DictamenService dictamenes;
    @Autowired TrayectoriaService trayectoria;
    @Autowired DocumentoService documentos;
    @Autowired PadreService padres;
    @Autowired TutorRepositorio tutores;
    @Autowired AlumnoTutorRepositorio vinculos;
    @Autowired UsuarioRepositorio usuarios;
    @Autowired PasswordEncoder encoder;
    @Autowired mx.gob.controlescolar.personas.aplicacion.ExpedienteService expedientes;
    @Autowired AccesoTutorService accesosTutor;
    @Autowired mx.gob.controlescolar.asistencia.aplicacion.AsistenciaService asistencia;

    @Test
    void basicaNoCreaCalendarioYMediaSi() {
        calendarios.sembrarBasica2025();
        Institucion basica = escuelas.alta("Basica cal", null, false, EnumSet.allOf(Modulo.class), "basicacal", "x");
        Institucion media = escuelas.alta("Media cal", null, true, EnumSet.allOf(Modulo.class), "mediacal", "x");
        Long ciclo = calendarios.ciclos().get(0).getId();
        assertThrows(NegocioException.class, () -> calendarios.crearDeEscuela(basica.getId(), ciclo, "PRIMARIA", null, 185));
        assertNotNull(calendarios.crearDeEscuela(media.getId(), ciclo, "MEDIA_SUPERIOR", "semestre", 190));
        assertEquals(185, calendarios.oficiales().get(0).getDiasEfectivos());
    }

    @Test
    void basicaNoCambiaEsquemaYMediaSi() {
        PlanVersion basica = planes.publicarConMomentos("PRIMARIA", "Oficial", "BIMESTRAL",
                BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("6"), false, false, 2,
                List.of(new PlanService.DefinicionMomento("Parcial", TipoMomento.ORDINARIO)));
        assertThrows(NegocioException.class, () -> planes.cambiarAprobatoria(basica.getId(), new BigDecimal("7")));
        assertThrows(NegocioException.class, () -> planes.configurarEsquema(basica.getId(), new BigDecimal("7"), 3, true, false));

        PlanVersion media = planes.publicarConMomentos("MEDIA_SUPERIOR", "Bachillerato local", "SEMESTRAL",
                BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("6"), false, false, null,
                List.of(new PlanService.DefinicionMomento("Ordinario", TipoMomento.ORDINARIO)));
        planes.cambiarAprobatoria(media.getId(), new BigDecimal("7"));
        planes.configurarEsquema(media.getId(), new BigDecimal("7"), 2, false, true);
        assertEquals(new BigDecimal("7"), planes.obtener(media.getId()).getAprobatoria());
        assertTrue(planes.momentosDe(media.getId()).stream().noneMatch(momento -> momento.getTipo() == TipoMomento.EXTRAORDINARIO));
        assertThrows(NegocioException.class, () -> planes.exigirAprobada(media.getId(), new BigDecimal("6.5")));
    }

    @Test
    void ventanaBajaYExtraordinario() {
        Institucion escuela = escuelas.alta("Ventana", null, true, EnumSet.allOf(Modulo.class), "ventana", "x");
        PlanVersion plan = planes.publicarConMomentos("MEDIA_SUPERIOR", "Ventanas", "SEMESTRAL",
                BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("6"), false, false, 1,
                List.of(new PlanService.DefinicionMomento("Ordinario", TipoMomento.ORDINARIO)));
        var periodo = planes.agregarPeriodo(plan.getId(), 1, "1 semestre");
        var programa = programas.adoptar(escuela.getId(), plan.getId(), "V");
        Grupo grupo = grupos.registrar(escuela.getId(), programa.getId(), "1A", 1);
        Alumno alumno = alumnos.registrar(escuela.getId(), grupo.getId(), "CURP110101HDFABC11", "Rita", "Paz", null);
        Long momento = planes.momentosDe(plan.getId()).get(0).getId();
        var ciclo = calendarios.asegurarCiclo("2026-2027", LocalDate.of(2026, 8, 1), LocalDate.of(2027, 7, 15));
        var calendario = calendarios.crearDeEscuela(escuela.getId(), ciclo.getId(), "MEDIA_SUPERIOR", "semestre", 190);
        var periodoCiclo = calendarios.abrirPeriodo(calendario.getId(), periodo.getId(),
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(10));
        calendarios.configurarVentana(periodoCiclo.getId(), momento,
                LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), LocalDate.now().minusDays(1));
        assertThrows(NegocioException.class, () -> calificaciones.capturar(
                escuela.getId(), alumno.getId(), "MAT", 1, momento, new BigDecimal("8"), null, null, false));

        calendarios.configurarVentana(periodoCiclo.getId(), momento,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        calificaciones.capturar(escuela.getId(), alumno.getId(), "MAT", 1, momento, new BigDecimal("8"), null, null, false);
        Tutor tutor = tutores.save(new Tutor(escuela.getId(), "Padre", usuario(escuela).getId()));
        vinculos.save(new AlumnoTutor(alumno.getId(), tutor.getId()));
        assertTrue(padres.consultar(escuela.getId(), tutor.getUsuarioId()).evaluaciones().isEmpty());

        alumnos.baja(escuela.getId(), alumno.getId());
        assertThrows(NegocioException.class, () -> calificaciones.capturar(
                escuela.getId(), alumno.getId(), "MAT", 1, momento, new BigDecimal("9"), null, null, false));
    }

    @Test
    void dictamenCctPromocionCreditosYKardex() {
        assertThrows(NegocioException.class, () -> escuelas.altaNivel("Sin cct", null, "MEDIA_SUPERIOR",
                "PARTICULAR", EnumSet.allOf(Modulo.class), "sincct", "x", null, null));
        assertThrows(NegocioException.class, () -> escuelas.altaNivel("Cct mala", "XXXX", "MEDIA_SUPERIOR",
                "PARTICULAR", EnumSet.allOf(Modulo.class), "cctmala", "x", null, null));
        Institucion media = escuelas.altaNivel("Bachilleres", "15PBH0001T", "MEDIA_SUPERIOR", "PARTICULAR",
                EnumSet.allOf(Modulo.class), "bach", "x", null, null);
        Institucion superior = escuelas.altaNivel("Universidad", "15PUN0001S", "SUPERIOR", "PARTICULAR",
                EnumSet.allOf(Modulo.class), "uni", "x", null, null);
        assertEquals("SUPERIOR", superior.getNivel());

        Institucion primaria = escuelas.altaNivel("Primaria CCT", "15DPR0001Y", "PRIMARIA", "FEDERAL",
                EnumSet.allOf(Modulo.class), "pricct", "x", null, null);
        Institucion otra = escuelas.alta("Secundaria CCT", null, true, EnumSet.allOf(Modulo.class), "seccct", "x");
        centros.compartirPlantel(otra.getId(), primaria.getPlantel().getId());
        centros.registrar(otra.getId(), "15PES0001W", "PARTICULAR", "Secundaria");
        assertEquals(2, centros.delPlantel(primaria.getId()).size());

        PlanVersion preescolar = planes.publicarConMomentos("PREESCOLAR", "Preescolar local", "BIMESTRAL",
                null, null, null, true, true, null,
                List.of(new PlanService.DefinicionMomento("Observación", TipoMomento.OBSERVACION)));
        assertThrows(NegocioException.class, () -> programas.adoptar(otra.getId(), preescolar.getId(), "No"));

        PlanVersion plan = planes.publicarConMomentos("MEDIA_SUPERIOR", "Tecnologico", "SEMESTRAL",
                BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("6"), false, false, 0,
                List.of(
                        new PlanService.DefinicionMomento("Ordinario", TipoMomento.ORDINARIO),
                        new PlanService.DefinicionMomento("Extraordinario", TipoMomento.EXTRAORDINARIO)));
        planes.agregarAsignatura(plan.getId(), "INF", "Informática", 4, 8, false, "MOD1", null);
        planes.agregarAsignatura(plan.getId(), "RED", "Redes", 4, 4, false, "MOD1", null);
        planes.agregarAsignatura(plan.getId(), "FIS", "Física", 4, 6, false, null, null);
        planes.definirUmbralCreditos(plan.getId(), 10);
        var programa = programas.adoptar(media.getId(), plan.getId(), "Tec");
        Grupo grupo = grupos.registrar(media.getId(), programa.getId(), "1A", 1);
        Alumno alumno = alumnos.registrar(media.getId(), grupo.getId(), "CURP120101HDFABC12", "Omar", "León", null);
        Long ordinario = planes.momentosDe(plan.getId()).stream()
                .filter(momento -> momento.getTipo() == TipoMomento.ORDINARIO).findFirst().orElseThrow().getId();
        calificaciones.capturar(media.getId(), alumno.getId(), "INF", 1, ordinario, new BigDecimal("8"), null, null, false);
        calificaciones.capturar(media.getId(), alumno.getId(), "RED", 1, ordinario, new BigDecimal("6"), null, null, false);
        calificaciones.capturar(media.getId(), alumno.getId(), "FIS", 1, ordinario, new BigDecimal("5"), null, null, false);
        assertEquals(new BigDecimal("7.0"), calificaciones.promedioModulo(alumno.getId(), plan.getId(), "MOD1", 1));
        assertEquals(12, trayectoria.creditosAcumulados(alumno.getId(), plan.getId()));
        assertTrue(trayectoria.elegiblePorCreditos(alumno.getId(), planes.obtener(plan.getId())));
        Grupo siguiente = grupos.registrar(media.getId(), programa.getId(), "2A", 2);
        assertThrows(NegocioException.class, () -> trayectoria.promover(media.getId(), alumno.getId(), siguiente.getId()));

        assertThrows(NegocioException.class, () -> calificaciones.capturarExtraordinario(
                media.getId(), alumno.getId(), "FIS", 1, new BigDecimal("7"), 2, "CALIFICACION", true, "A-2"));
        calificaciones.capturarExtraordinario(media.getId(), alumno.getId(), "FIS", 1, new BigDecimal("5"), 1,
                "CALIFICACION", false, "A-1");
        assertThrows(NegocioException.class, () -> calificaciones.capturarExtraordinario(
                media.getId(), alumno.getId(), "FIS", 1, new BigDecimal("7"), 2, "FALTAS", true, "A-2"));
        calificaciones.capturarExtraordinario(media.getId(), alumno.getId(), "FIS", 1, new BigDecimal("7"), 1,
                "CALIFICACION", true, "A-1");
        calificaciones.capturarExtraordinario(media.getId(), alumno.getId(), "FIS", 1, new BigDecimal("7"), 2,
                "FALTAS", true, "A-2");

        PlanVersion sinExtra = planes.publicarConMomentos("MEDIA_SUPERIOR", "Sin extra", "SEMESTRAL",
                BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("6"), false, false, null,
                List.of(new PlanService.DefinicionMomento("Ordinario", TipoMomento.ORDINARIO)));
        var programaSin = programas.adoptar(media.getId(), sinExtra.getId(), "Sin");
        Grupo grupoSin = grupos.registrar(media.getId(), programaSin.getId(), "1B", 1);
        Alumno otro = alumnos.registrar(media.getId(), grupoSin.getId(), "CURP130101HDFABC13", "Lia", "Núñez", null);
        assertThrows(NegocioException.class, () -> calificaciones.capturarExtraordinario(
                media.getId(), otro.getId(), "MAT", 1, new BigDecimal("6"), 1, "FALTAS", true, "A"));

        assertThrows(NegocioException.class, () -> dictamenes.revalidar(media.getId(), alumno.getId(), "1er semestre",
                "Liceo", "2020", "2022", "Extranjero", " ", "E1", "F1", LocalDate.now(), "HIS"));
        assertThrows(NegocioException.class, () -> dictamenes.equivaler(media.getId(), alumno.getId(), "3er semestre",
                "CBT", "2022", "2024", "SE", "México", "E2", new BigDecimal("5.9"), LocalDate.now(), "QUI"));
        dictamenes.equivaler(media.getId(), alumno.getId(), "3er semestre", "CBT origen", "2022", "2024",
                "SE", "México", "E2", new BigDecimal("8.5"), LocalDate.now(), "QUI");
        String kardex = documentos.texto(media.getId(), alumno.getId(), plan.getId(), false);
        assertTrue(kardex.contains("EQUIVALENCIA"));
        assertTrue(kardex.contains("QUI"));

        var generacion = trayectoria.crearGeneracion(media.getId(), "2024", 2024);
        trayectoria.asignarGeneracion(media.getId(), otro.getId(), grupoSin.getId(), generacion.getId());
        assertEquals("2024", alumnos.listar(media.getId()).stream()
                .filter(registrado -> registrado.getId().equals(otro.getId()))
                .findFirst().orElseThrow().getGeneracion().getNombre());

        var profesor = grupos.registrarProfesor(media.getId(), "Docente");
        var horario = grupos.asignar(media.getId(), grupo.getId(), profesor.getId(), "INF", "Lunes", "08:00", "09:00");
        assertNotNull(horario.getAsignacion());
        assertEquals("INF", horario.getAsignacion().getAsignaturaClave());
    }

    @Test
    void egresaEnElUltimoPeriodoConGeneracion() {
        Institucion escuela = escuelas.alta("Egreso", null, true, EnumSet.allOf(Modulo.class), "egreso", "x");
        PlanVersion plan = planes.publicarConMomentos("SUPERIOR", "Licenciatura local", "CUATRIMESTRAL",
                BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("7"), false, false, null,
                List.of(new PlanService.DefinicionMomento("Examen", TipoMomento.ORDINARIO)));
        planes.agregarPeriodo(plan.getId(), 1, "1 cuatrimestre");
        var programa = programas.adoptar(escuela.getId(), plan.getId(), "Lic");
        Grupo grupo = grupos.registrar(escuela.getId(), programa.getId(), "4A", 1);
        Alumno alumno = alumnos.registrar(escuela.getId(), grupo.getId(), "CURP140101HDFABC14", "Sara", "Gil", null);
        Long momento = planes.momentosDe(plan.getId()).get(0).getId();
        planes.agregarAsignatura(plan.getId(), "CAL", "Cálculo", 4, 8, false, null, null);
        planes.definirUmbralCreditos(plan.getId(), 8);
        calificaciones.capturar(escuela.getId(), alumno.getId(), "CAL", 1, momento, new BigDecimal("8"), null, null, false);
        var generacion = trayectoria.crearGeneracion(escuela.getId(), "2022", 2022);
        trayectoria.asignarGeneracion(escuela.getId(), alumno.getId(), grupo.getId(), generacion.getId());
        trayectoria.egresar(escuela.getId(), alumno.getId());
        assertEquals("EGRESADO", alumnos.listar(escuela.getId()).get(0).getEstatus());
        alumnos.reinscribir(escuela.getId(), alumno.getId());
        assertEquals("ACTIVO", alumnos.listar(escuela.getId()).get(0).getEstatus());
    }

    @Test
    void elGrupoNoAceptaMasAlumnosQueSuCapacidad() {
        Institucion escuela = escuelas.alta("Cupo", null, false, EnumSet.of(Modulo.ALUMNOS, Modulo.INSCRIPCION, Modulo.PLANES),
                "cupo", "x");
        PlanVersion plan = planes.publicarConMomentos("PRIMARIA", "Cupo", "BIMESTRAL",
                BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("6"), false, false, 2,
                List.of(new PlanService.DefinicionMomento("Parcial", TipoMomento.ORDINARIO)));
        var programa = programas.adoptar(escuela.getId(), plan.getId(), "Prim");
        Grupo grupo = grupos.registrar(escuela.getId(), programa.getId(), "1A", 1, "Norte", "12", 1);
        assertEquals("Norte", grupo.getEdificio());
        assertEquals("12", grupo.getAula());
        alumnos.registrar(escuela.getId(), grupo.getId(), "CURP150101HDFABC15", "Luz", "Ríos", null);
        assertThrows(NegocioException.class, () -> alumnos.registrar(escuela.getId(), grupo.getId(),
                "CURP150102HDFABC16", "Leo", "Ríos", null));
    }

    @Test
    void tutorEntraConMatriculaYLaListaEsDelGrupo() {
        Institucion escuela = escuelas.alta("Tutor", null, false,
                EnumSet.of(Modulo.ALUMNOS, Modulo.INSCRIPCION, Modulo.PLANES, Modulo.PADRES, Modulo.EVALUACION),
                "tutoracc", "x");
        PlanVersion plan = planes.publicarConMomentos("PRIMARIA", "Tutor", "BIMESTRAL",
                BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("6"), false, false, 2,
                List.of(new PlanService.DefinicionMomento("Parcial", TipoMomento.ORDINARIO)));
        var programa = programas.adoptar(escuela.getId(), plan.getId(), "Prim");
        Grupo grupo = grupos.registrar(escuela.getId(), programa.getId(), "1A", 1, "Sur", "1", 30);
        Alumno alumno = alumnos.registrar(escuela.getId(), grupo.getId(), "CURP160101HDFABC17", "Nora", "Gil", null);
        assertNotNull(alumno.getMatricula());
        expedientes.guardarResponsable(escuela.getId(), alumno.getId(),
                new mx.gob.controlescolar.personas.aplicacion.ExpedienteService.DatosResponsable(
                        null, "Ana", "Gil", null, "Madre", "5511111111", null, null, null, null, true, null));
        var acceso = accesosTutor.definir(escuela.getId(), alumno.getId(), null);
        assertEquals(alumno.getMatricula(), acceso.matricula());
        assertEquals(8, acceso.clave().length());
        accesosTutor.definir(escuela.getId(), alumno.getId(), "clave-tutor");
        var usuario = usuarios.findByLogin(alumno.getMatricula()).orElseThrow();
        assertTrue(usuario.esTutor());
        assertTrue(encoder.matches("clave-tutor", usuario.getPassword()));

        asistencia.registrarMatriz(escuela.getId(), grupo.getId(), LocalDate.now(), java.util.Map.of(alumno.getId(), false));
        assertEquals(1, asistencia.faltasAlumno(alumno.getId()));
    }

    private Usuario usuario(Institucion escuela) {
        return usuarios.save(new Usuario(escuela, "padre-" + escuela.getId(), encoder.encode("padre"), "Padre"));
    }
}
