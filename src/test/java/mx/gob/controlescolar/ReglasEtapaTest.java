package mx.gob.controlescolar;

import mx.gob.controlescolar.academico.aplicacion.PlanService;
import mx.gob.controlescolar.academico.aplicacion.ProgramaService;
import mx.gob.controlescolar.academico.dominio.PlanVersion;
import mx.gob.controlescolar.academico.dominio.TipoMomento;
import mx.gob.controlescolar.acceso.aplicacion.EscuelaService;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import mx.gob.controlescolar.asistencia.aplicacion.AsistenciaService;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.documentos.aplicacion.DocumentoService;
import mx.gob.controlescolar.evaluacion.aplicacion.CalificacionService;
import mx.gob.controlescolar.evaluacion.dominio.Calificacion;
import mx.gob.controlescolar.inscripcion.aplicacion.GrupoService;
import mx.gob.controlescolar.inscripcion.dominio.Grupo;
import mx.gob.controlescolar.padres.aplicacion.PadreService;
import mx.gob.controlescolar.personas.aplicacion.AlumnoService;
import mx.gob.controlescolar.personas.dominio.Alumno;
import mx.gob.controlescolar.personas.dominio.AlumnoTutor;
import mx.gob.controlescolar.personas.dominio.Inscripcion;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReglasEtapaTest {

    @Autowired EscuelaService escuelas;
    @Autowired ModuloGuardia modulos;
    @Autowired PlanService planes;
    @Autowired ProgramaService programas;
    @Autowired GrupoService grupos;
    @Autowired AlumnoService alumnos;
    @Autowired CalificacionService calificaciones;
    @Autowired AsistenciaService asistencia;
    @Autowired DocumentoService documentos;
    @Autowired PadreService padres;
    @Autowired PerfilService perfiles;
    @Autowired TutorRepositorio tutores;
    @Autowired AlumnoTutorRepositorio vinculos;
    @Autowired UsuarioRepositorio usuarios;
    @Autowired PasswordEncoder encoder;

    @Test
    void moduloApagadoNiegaLaOperacion() {
        Institucion escuela = escuelas.alta("Norte", null, false, EnumSet.of(Modulo.ALUMNOS), "norte", "norte");
        assertThrows(NegocioException.class, () -> modulos.exigir(escuela.getId(), Modulo.PLANES));
    }

    @Test
    void versionConCalificacionNoSeEdita() {
        Institucion escuela = escuelaCompleta("Uno");
        PlanVersion plan = planNumerico("Plan A", new BigDecimal("6"),
                List.of(new PlanService.DefinicionMomento("Parcial", TipoMomento.ORDINARIO)));
        var programa = programas.adoptar(escuela.getId(), plan.getId(), "A");
        Grupo grupo = grupos.registrar(escuela.getId(), programa.getId(), "1A", 1);
        Alumno alumno = alumnos.registrar(escuela.getId(), grupo.getId(), "CURP010101HDFABC01", "Ana", "López", null);
        Long momento = planes.momentosDe(plan.getId()).get(0).getId();
        calificaciones.capturar(escuela.getId(), alumno.getId(), "ESP", 1, momento, new BigDecimal("8"), null, null, false);
        assertThrows(NegocioException.class, () -> planes.cambiarAprobatoria(plan.getId(), new BigDecimal("7")));
    }

    @Test
    void grupoDeOtraEscuelaNoAparece() {
        Institucion una = escuelaCompleta("Una");
        Institucion otra = escuelaCompleta("Otra");
        PlanVersion plan = planNumerico("Comun", new BigDecimal("6"),
                List.of(new PlanService.DefinicionMomento("Parcial", TipoMomento.ORDINARIO)));
        var programa = programas.adoptar(una.getId(), plan.getId(), "P");
        grupos.registrar(una.getId(), programa.getId(), "1A", 1);
        assertTrue(grupos.consultar(otra.getId()).isEmpty());
    }

    @Test
    void cambioDePlanConservaCalificacionesAnteriores() {
        Institucion escuela = escuelaCompleta("Cambio");
        PlanVersion primero = planNumerico("Primero", new BigDecimal("6"),
                List.of(new PlanService.DefinicionMomento("Parcial", TipoMomento.ORDINARIO)));
        PlanVersion segundo = planNumerico("Segundo", new BigDecimal("7"),
                List.of(new PlanService.DefinicionMomento("Examen", TipoMomento.ORDINARIO)));
        var programa = programas.adoptar(escuela.getId(), primero.getId(), "P");
        Grupo grupo = grupos.registrar(escuela.getId(), programa.getId(), "1A", 1);
        Alumno alumno = alumnos.registrar(escuela.getId(), grupo.getId(), "CURP020101HDFABC02", "Luis", "Ruiz", null);
        Long momento = planes.momentosDe(primero.getId()).get(0).getId();
        calificaciones.capturar(escuela.getId(), alumno.getId(), "ESP", 1, momento, new BigDecimal("9"), null, null, false);
        Inscripcion nueva = alumnos.cambiarPlan(escuela.getId(), alumno.getId(), segundo.getId());
        List<Calificacion> anteriores = calificaciones.delAlumno(alumno.getId(), primero.getId());
        assertEquals(primero.getId(), anteriores.get(0).getPlanVersionId());
        assertEquals(segundo.getId(), nueva.getPlanVersion().getId());
    }

    @Test
    void momentoFueraDelEsquemaSeRechaza() {
        Institucion escuela = escuelaCompleta("Esquema");
        PlanVersion tres = planNumerico("Tres", new BigDecimal("7"), List.of(
                new PlanService.DefinicionMomento("Uno", TipoMomento.ORDINARIO),
                new PlanService.DefinicionMomento("Dos", TipoMomento.ORDINARIO),
                new PlanService.DefinicionMomento("Tres", TipoMomento.ORDINARIO)));
        PlanVersion otro = planNumerico("Otro", new BigDecimal("6"),
                List.of(new PlanService.DefinicionMomento("Ajeno", TipoMomento.ORDINARIO)));
        var programa = programas.adoptar(escuela.getId(), tres.getId(), "P");
        Grupo grupo = grupos.registrar(escuela.getId(), programa.getId(), "1A", 1);
        Alumno alumno = alumnos.registrar(escuela.getId(), grupo.getId(), "CURP030101HDFABC03", "Mia", "Sol", null);
        Long ajeno = planes.momentosDe(otro.getId()).get(0).getId();
        assertThrows(NegocioException.class, () -> calificaciones.capturar(
                escuela.getId(), alumno.getId(), "MAT", 1, ajeno, new BigDecimal("8"), null, null, false));
    }

    @Test
    void faltaDeAlumnoNoCreaFaltaDeProfesor() {
        Institucion escuela = escuelaCompleta("Faltas");
        PlanVersion plan = planNumerico("F", new BigDecimal("6"),
                List.of(new PlanService.DefinicionMomento("Parcial", TipoMomento.ORDINARIO)));
        var programa = programas.adoptar(escuela.getId(), plan.getId(), "P");
        Grupo grupo = grupos.registrar(escuela.getId(), programa.getId(), "1A", 1);
        Alumno alumno = alumnos.registrar(escuela.getId(), grupo.getId(), "CURP040101HDFABC04", "Eva", "Paz", null);
        var profesor = grupos.registrarProfesor(escuela.getId(), "Profe");
        asistencia.registrarLista(escuela.getId(), grupo.getId(), LocalDate.now(), alumno.getId(), false);
        assertEquals(1, asistencia.faltasAlumno(alumno.getId()));
        assertEquals(0, asistencia.faltasProfesor(escuela.getId(), profesor.getId()));
    }

    @Test
    void constanciaEsDeLaEscuelaYSinFirma() {
        Institucion escuela = escuelaCompleta("Papel");
        PlanVersion plan = planNumerico("Doc", new BigDecimal("6"),
                List.of(new PlanService.DefinicionMomento("Parcial", TipoMomento.ORDINARIO)));
        var programa = programas.adoptar(escuela.getId(), plan.getId(), "P");
        Grupo grupo = grupos.registrar(escuela.getId(), programa.getId(), "1A", 1);
        Alumno alumno = alumnos.registrar(escuela.getId(), grupo.getId(), "CURP050101HDFABC05", "Nora", "Gil", null);
        Long momento = planes.momentosDe(plan.getId()).get(0).getId();
        calificaciones.capturar(escuela.getId(), alumno.getId(), "ESP", 1, momento, new BigDecimal("8"), null, null, false);
        calificaciones.capturar(escuela.getId(), alumno.getId(), "ART", 1, momento, new BigDecimal("9"), null, null, true);
        String oficial = documentos.texto(escuela.getId(), alumno.getId(), plan.getId(), false);
        String boleta = documentos.texto(escuela.getId(), alumno.getId(), plan.getId(), true);
        assertTrue(oficial.contains("Papel"));
        assertTrue(oficial.contains("ESP"));
        assertFalse(oficial.contains("ART"));
        assertTrue(boleta.contains("ART"));
        assertFalse(oficial.toLowerCase().contains("firma"));
        assertFalse(oficial.toLowerCase().contains("sello"));
        documentos.constancia(escuela.getId(), alumno.getId(), plan.getId());
        documentos.boleta(escuela.getId(), alumno.getId(), plan.getId());
    }

    @Test
    void tutorSoloVeASusHijosSiElModuloEstaActivo() {
        Institucion apagada = escuelas.alta("Sin padres", null, false,
                EnumSet.of(Modulo.ALUMNOS, Modulo.EVALUACION), "sin", "sin");
        assertThrows(NegocioException.class, () -> padres.consultar(apagada.getId(), 1L));

        Institucion escuela = escuelas.alta("Con padres", null, true, EnumSet.allOf(Modulo.class), "con", "con");
        PlanVersion plan = planNumerico("Padres", new BigDecimal("6"),
                List.of(new PlanService.DefinicionMomento("Parcial", TipoMomento.ORDINARIO)));
        var programa = programas.adoptar(escuela.getId(), plan.getId(), "P");
        Grupo grupo = grupos.registrar(escuela.getId(), programa.getId(), "1A", 1);
        Alumno hijo = alumnos.registrar(escuela.getId(), grupo.getId(), "CURP060101HDFABC06", "Leo", "Mar", null);
        Alumno otro = alumnos.registrar(escuela.getId(), grupo.getId(), "CURP070101HDFABC07", "Teo", "Mar", null);
        Tutor tutor = tutores.save(new Tutor(escuela.getId(), "Mamá", usuarioTutor(escuela).getId()));
        vinculos.save(new AlumnoTutor(hijo.getId(), tutor.getId()));
        Long momento = planes.momentosDe(plan.getId()).get(0).getId();
        calificaciones.capturar(escuela.getId(), hijo.getId(), "ESP", 1, momento, new BigDecimal("8"), null, null, false);
        calificaciones.capturar(escuela.getId(), otro.getId(), "ESP", 1, momento, new BigDecimal("5"), null, null, false);
        PadreService.Consulta consulta = padres.consultar(escuela.getId(), tutor.getUsuarioId());
        assertEquals(1, consulta.evaluaciones().size());
        assertEquals(hijo.getId(), consulta.evaluaciones().get(0).getAlumnoId());
    }

    @Test
    void perfilNoAutorizaUnModuloApagado() {
        Institucion escuela = escuelas.alta("Perfil", null, false, EnumSet.of(Modulo.ALUMNOS), "perfilesc", "perfilesc");
        assertThrows(NegocioException.class,
                () -> perfiles.crear(escuela.getId(), "Docente", Set.of("PADRES_CONSULTAR")));
    }

    private Usuario usuarioTutor(Institucion escuela) {
        return usuarios.save(new Usuario(escuela, "mama-" + escuela.getId(), encoder.encode("mama"), "Mamá"));
    }

    private Institucion escuelaCompleta(String nombre) {
        return escuelas.alta(nombre, null, false, EnumSet.allOf(Modulo.class),
                nombre.toLowerCase().replace(" ", ""), "clave");
    }

    private PlanVersion planNumerico(String nombre, BigDecimal aprobatoria, List<PlanService.DefinicionMomento> momentos) {
        return planes.publicarConMomentos("PRIMARIA", nombre, "BIMESTRAL",
                BigDecimal.ZERO, BigDecimal.TEN, aprobatoria, false, false, 2, momentos);
    }
}
