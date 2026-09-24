package mx.gob.controlescolar.personas;

import mx.gob.controlescolar.acceso.aplicacion.EscuelaService;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.academico.aplicacion.PlanService;
import mx.gob.controlescolar.academico.aplicacion.ProgramaService;
import mx.gob.controlescolar.academico.dominio.TipoMomento;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.inscripcion.aplicacion.GrupoService;
import mx.gob.controlescolar.personas.aplicacion.Apoyo911Service;
import mx.gob.controlescolar.personas.aplicacion.AlumnoService;
import mx.gob.controlescolar.personas.aplicacion.ExpedienteService;
import mx.gob.controlescolar.personas.dominio.Alumno;
import mx.gob.controlescolar.personas.dominio.Discapacidad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExpedienteTest {

    @Autowired EscuelaService escuelas;
    @Autowired PlanService planes;
    @Autowired ProgramaService programas;
    @Autowired GrupoService grupos;
    @Autowired AlumnoService alumnos;
    @Autowired ExpedienteService expedientes;
    @Autowired Apoyo911Service apoyo911;

    @Test
    void guardaLentesDiscapacidadYResponsable() {
        Institucion escuela = escuelas.alta("Exp", null, false, EnumSet.allOf(Modulo.class), "exp", "exp");
        var plan = planes.publicarConMomentos("PRIMARIA", "P", "BIMESTRAL",
                BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("6"), false, false, 2,
                List.of(new PlanService.DefinicionMomento("Parcial", TipoMomento.ORDINARIO)));
        var programa = programas.adoptar(escuela.getId(), plan.getId(), "P");
        var grupo = grupos.registrar(escuela.getId(), programa.getId(), "1A", 1);
        Alumno alumno = alumnos.registrar(escuela.getId(), grupo.getId(), "CURP080101HDFABC08", "Ana", "Ruiz", null);

        Discapacidad visual = expedientes.catalogoDiscapacidades().stream()
                .filter(item -> item.getCodigo().equals("VISUAL"))
                .findFirst().orElseThrow();
        expedientes.guardarDatos(escuela.getId(), alumno.getId(), "Ana", "Ruiz", "López", "M",
                LocalDate.of(2018, 1, 8), true, false, Set.of(visual.getId()));
        expedientes.guardarResponsable(escuela.getId(), alumno.getId(),
                new ExpedienteService.DatosResponsable("TUTO080101MDFABC08", "Laura", "Ruiz", null, "Madre",
                        "555", "556", "laura@ejemplo.mx", "Secundaria", "Comercio", true, null));

        var expediente = expedientes.consultar(escuela.getId(), alumno.getId());
        assertTrue(expediente.alumno().isUsaLentes());
        assertEquals(1, expediente.discapacidades().size());
        assertEquals("VISUAL", expediente.discapacidades().get(0).getCodigo());
        assertEquals("Laura Ruiz", expediente.responsables().get(0).nombreCompleto());

        var resumen = apoyo911.resumen(escuela.getId());
        assertEquals(1, resumen.usanLentes());
        assertEquals(1, resumen.conDiscapacidad());
    }

    @Test
    void noAceptaMasDeTresDiscapacidades() {
        Institucion escuela = escuelas.alta("Lim", null, false, EnumSet.allOf(Modulo.class), "lim", "lim");
        var plan = planes.publicarConMomentos("PRIMARIA", "L", "BIMESTRAL",
                BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("6"), false, false, 2,
                List.of(new PlanService.DefinicionMomento("Parcial", TipoMomento.ORDINARIO)));
        var programa = programas.adoptar(escuela.getId(), plan.getId(), "L");
        var grupo = grupos.registrar(escuela.getId(), programa.getId(), "1A", 1);
        Alumno alumno = alumnos.registrar(escuela.getId(), grupo.getId(), "CURP090101HDFABC09", "Bea", "Sol", null);
        Set<Long> muchas = expedientes.catalogoDiscapacidades().stream().map(Discapacidad::getId).limit(4)
                .collect(java.util.stream.Collectors.toSet());
        assertThrows(NegocioException.class, () ->
                expedientes.guardarDatos(escuela.getId(), alumno.getId(), "Bea", "Sol", null, null, null,
                        false, false, muchas));
    }
}
