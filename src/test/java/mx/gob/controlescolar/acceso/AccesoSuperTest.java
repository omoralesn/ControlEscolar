package mx.gob.controlescolar.acceso;

import mx.gob.controlescolar.acceso.aplicacion.AccesoService;
import mx.gob.controlescolar.acceso.aplicacion.CentroTrabajoService;
import mx.gob.controlescolar.acceso.aplicacion.EscuelaService;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import mx.gob.controlescolar.academico.web.PlanController;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;

import java.time.LocalDate;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AccesoSuperTest {

    @Autowired EscuelaService escuelas;
    @Autowired CentroTrabajoService centros;
    @Autowired AccesoService acceso;
    @Autowired PerfilService perfiles;
    @Autowired UsuarioRepositorio usuarios;
    @Autowired PlanController planes;
    @Autowired MockMvc mvc;

    @Test
    @WithMockUser(roles = "PLATAFORMA")
    void adminNoAbreElAcceso() throws Exception {
        mvc.perform(get("/acceso")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SUPER")
    void superAbreElAcceso() throws Exception {
        mvc.perform(get("/acceso")).andExpect(status().isOk());
    }

    @Test
    void superAsignaCctYNoPublicaPlanes() {
        var escuela = escuelas.alta("Bachillerato", null, true, EnumSet.of(Modulo.PLANES, Modulo.ALUMNOS),
                "bachsuper", "clave");
        centros.registrar(escuela.getId(), "15PBH0001T", "PARTICULAR", "Bachillerato");
        assertEquals("MEDIA_SUPERIOR", escuela.getNivel());
        assertEquals("15", centros.deLaEscuela(escuela.getId()).get(0).getEntidadClave());

        Usuario superusuario = usuarios.save(Usuario.superusuario("super-prueba", "hash", "Super"));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(superusuario, null, superusuario.getAuthorities()));
        assertThrows(NegocioException.class, () -> planes.listar(new ExtendedModelMap()));
        SecurityContextHolder.clearContext();
    }

    @Test
    void apagarModuloQuitaElPermisoYLosCierresBloqueanLaEntrada() {
        var escuela = escuelas.alta("Acceso", null, false, EnumSet.of(Modulo.PLANES, Modulo.ALUMNOS),
                "accesoesc", "clave");
        Usuario escolar = usuarios.findByLogin("accesoesc").orElseThrow();
        assertTrue(perfiles.autorizado(escolar.getId(), "PLANES_CONSULTAR"));
        escuelas.definirModulos(escuela.getId(), EnumSet.of(Modulo.ALUMNOS));
        assertFalse(perfiles.autorizado(escolar.getId(), "PLANES_CONSULTAR"));
        assertTrue(perfiles.autorizado(escolar.getId(), "ALUMNOS_CONSULTAR"));

        acceso.definirVigencia(escolar.getId(), null, LocalDate.now().minusDays(1));
        assertFalse(escolar.isEnabled());
        acceso.definirVigencia(escolar.getId(), null, null);
        assertTrue(escolar.isEnabled());

        escuelas.suspender(escuela.getId());
        assertFalse(escolar.isEnabled());
        escuelas.activar(escuela.getId());
        assertTrue(escolar.isEnabled());

        var conCct = escuelas.altaNivel("Primaria federal", "15DPR0001Y", false, "PRIMARIA", "FEDERAL",
                EnumSet.of(Modulo.ALUMNOS), "primfed", "clave");
        Usuario dePrimaria = usuarios.findByLogin("primfed").orElseThrow();
        Usuario deOtra = usuarios.save(new Usuario(escuela, "otra-cct", "hash", "Otra"));
        centros.compartirPlantel(escuela.getId(), conCct.getPlantel().getId());
        acceso.suspenderPlantel(conCct.getPlantel().getId());
        assertFalse(dePrimaria.isEnabled());
        assertFalse(deOtra.isEnabled());
    }
}
