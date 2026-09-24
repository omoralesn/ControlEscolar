package mx.gob.controlescolar.academico;

import mx.gob.controlescolar.academico.aplicacion.PlanService;
import mx.gob.controlescolar.academico.dominio.AsignaturaPlan;
import mx.gob.controlescolar.academico.dominio.CatalogoPlanesSep;
import mx.gob.controlescolar.academico.dominio.PlanVersion;
import mx.gob.controlescolar.academico.dominio.TipoMomento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PlanesSepTest {

    @Autowired PlanService planes;

    @Test
    void elPlanGeneralTraeLasAsignaturasDeLaSep() {
        PlanVersion preescolar = planes.publicarPlanGeneral(CatalogoPlanesSep.PREESCOLAR, null, null, null,
                true, true, null, List.of(new PlanService.DefinicionMomento("Observación", TipoMomento.OBSERVACION)));
        List<AsignaturaPlan> materias = planes.asignaturasDe(preescolar.getId());

        assertEquals(4, materias.size());
        assertTrue(materias.stream().allMatch(AsignaturaPlan::isOficial));
        assertTrue(materias.stream().anyMatch(materia -> materia.getClave().equals("LEN")
                && materia.getNombre().equals("Lenguajes")));
        assertTrue(materias.stream().noneMatch(materia -> materia.getClave().equals("FIS")));
    }

    @Test
    void primariaTraeLasDisciplinasDelPlan2022() {
        PlanVersion primaria = planes.publicarPlanGeneral(CatalogoPlanesSep.PRIMARIA,
                BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("6"), false, false, 2,
                List.of(new PlanService.DefinicionMomento("Parcial", TipoMomento.ORDINARIO)));
        List<AsignaturaPlan> materias = planes.asignaturasDe(primaria.getId());
        assertTrue(materias.size() >= 10);
        assertTrue(materias.stream().anyMatch(materia -> materia.getClave().equals("ESP")));
        assertTrue(materias.stream().anyMatch(materia -> materia.getClave().equals("ART")));
    }
}
