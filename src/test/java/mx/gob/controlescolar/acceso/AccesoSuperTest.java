package mx.gob.controlescolar.acceso;

import mx.gob.controlescolar.acceso.aplicacion.AccesoService;
import mx.gob.controlescolar.acceso.aplicacion.AuditoriaService;
import mx.gob.controlescolar.acceso.aplicacion.CentroTrabajoService;
import mx.gob.controlescolar.acceso.aplicacion.EscuelaService;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import mx.gob.controlescolar.academico.web.PlanController;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.comun.dominio.Estado;
import mx.gob.controlescolar.comun.dominio.Localidad;
import mx.gob.controlescolar.comun.dominio.Municipio;
import mx.gob.controlescolar.comun.persistencia.EstadoRepositorio;
import mx.gob.controlescolar.comun.persistencia.LocalidadRepositorio;
import mx.gob.controlescolar.comun.persistencia.MunicipioRepositorio;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AccesoSuperTest {

    @Autowired EscuelaService escuelas;
    @Autowired CentroTrabajoService centros;
    @Autowired AccesoService acceso;
    @Autowired PerfilService perfiles;
    @Autowired AuditoriaService auditoria;
    @Autowired UsuarioRepositorio usuarios;
    @Autowired mx.gob.controlescolar.acceso.aplicacion.UsuarioDetalleService detalle;
    @Autowired org.springframework.security.crypto.password.PasswordEncoder encoder;
    @Autowired PlanController planes;
    @Autowired MockMvc mvc;
    @Autowired EstadoRepositorio estados;
    @Autowired MunicipioRepositorio municipios;
    @Autowired LocalidadRepositorio localidades;

    @Test
    @WithMockUser(roles = "PLATAFORMA")
    void adminNoAbreElAcceso() throws Exception {
        mvc.perform(get("/acceso")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SUPER")
    void superAbreElAcceso() throws Exception {
        mvc.perform(get("/acceso")).andExpect(status().isOk());
        mvc.perform(get("/acceso/escuelas")).andExpect(status().isOk());
        mvc.perform(get("/acceso/usuarios")).andExpect(status().isOk());
        mvc.perform(get("/acceso/perfiles")).andExpect(status().isOk());
        mvc.perform(get("/acceso/planteles")).andExpect(status().isOk());
        mvc.perform(get("/acceso/plantilla")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUPER")
    void revisarCctMuestraDatosDerivadosSinGuardar() throws Exception {
        mvc.perform(post("/acceso/escuelas/revisar")
                        .with(csrf())
                        .param("nombre", "Primaria revisión")
                        .param("claveCct", "15DPR0001Y")
                        .param("sostenimiento", "FEDERAL")
                        .param("habilitados", "ALUMNOS"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PRIMARIA")))
                .andExpect(content().string(containsString("FEDERAL")))
                .andExpect(content().string(containsString("0001")));
        assertTrue(escuelas.listar().stream().noneMatch(e -> "Primaria revisión".equals(e.getNombre())));
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
        assertFalse(escolar.isAccountNonExpired());
        assertTrue(escolar.isEnabled());
        acceso.definirVigencia(escolar.getId(), null, null);
        assertTrue(escolar.isAccountNonExpired());
        assertTrue(escolar.isEnabled());

        escuelas.suspender(escuela.getId());
        assertFalse(escolar.isEnabled());
        escuelas.activar(escuela.getId());
        assertTrue(escolar.isEnabled());

        var conCct = escuelas.altaNivel("Primaria federal", "15DPR0001Y", "PRIMARIA", "FEDERAL",
                EnumSet.of(Modulo.ALUMNOS), "primfed", "clave", null, null);
        Usuario dePrimaria = usuarios.findByLogin("primfed").orElseThrow();
        Usuario deOtra = usuarios.save(new Usuario(escuela, "otra-cct", "hash", "Otra"));
        centros.compartirPlantel(escuela.getId(), conCct.getPlantel().getId());
        acceso.suspenderPlantel(conCct.getPlantel().getId());
        assertFalse(dePrimaria.isEnabled());
        assertFalse(deOtra.isEnabled());
    }

    @Test
    void sostenimientoParticularSinCheckYDomicilioPorCp() {
        Estado estado = estados.save(new Estado("México"));
        Municipio municipio = municipios.save(new Municipio(estado, "Toluca"));
        Localidad localidad = localidades.save(new Localidad(municipio, "Centro", "50000"));

        assertTrue(EscuelaService.esParticular("PARTICULAR"));
        assertFalse(EscuelaService.esParticular("FEDERAL"));

        var escuela = escuelas.altaNivel("Con domicilio", "15PPR0001C", "PRIMARIA", "PARTICULAR",
                EnumSet.of(Modulo.ALUMNOS, Modulo.PLANTILLA), "domesc", "clave",
                new EscuelaService.DatosDomicilio("Av. Independencia", "100", null, "Centro",
                        "50000", null, localidad.getId()),
                new EscuelaService.DatosContacto("Dirección escolar", "7221112233", null, "contacto@escuela.mx"));

        assertTrue(escuela.isParticular());
        assertEquals("Dirección escolar", escuela.getContactoNombre());
        assertEquals("7221112233", escuela.getTelefono());
        assertEquals("contacto@escuela.mx", escuela.getCorreo());
        assertEquals("50000", escuela.getDomicilio().getCodigoPostal());
        assertEquals(municipio.getId(), escuela.getDomicilio().getMunicipio().getId());
        assertEquals(estado.getId(), escuela.getDomicilio().getEstado().getId());
        assertEquals(1, escuelas.localidadesPorCp("50000").size());
    }

    @Test
    void altaPorCctDerivaNivelYSostenimiento() {
        var escuela = escuelas.altaPorCct("Primaria CCT", "15dpr0001y", "FEDERAL",
                EnumSet.of(Modulo.ALUMNOS, Modulo.PLANTILLA));
        assertEquals("15DPR0001Y", escuela.getClaveCct());
        assertEquals("PRIMARIA", escuela.getNivel());
        assertFalse(escuela.isParticular());
        var centro = centros.deLaEscuela(escuela.getId()).get(0);
        assertEquals("15", centro.getEntidadClave());
        assertEquals("D", centro.getClasificador());
        assertEquals("FEDERAL", centro.getSostenimiento());
        assertEquals("PR", centro.getIdentificador());
        assertEquals("0001", centro.getProgresivo());
        assertTrue(acceso.deLaEscuela(escuela.getId()).isEmpty());
        assertEquals(1, perfiles.deLaEscuela(escuela.getId()).size());

        var adminPerfil = perfiles.deLaEscuela(escuela.getId()).get(0);
        perfiles.crearUsuarioPorSuper(escuela.getId(), "cctprim", "clave", "Admin Uno", adminPerfil.getId(), false);
        perfiles.crearUsuarioPorSuper(escuela.getId(), "cctprim2", "clave", "Admin Dos", adminPerfil.getId(), false);
        assertEquals(2, acceso.deLaEscuela(escuela.getId()).size());
        assertTrue(auditoria.deLaEscuela(escuela.getId()).stream()
                .anyMatch(e -> "ALTA_USUARIO_SUPER".equals(e.getAccion()) && "cctprim".equals(e.getAfectadoLogin())));
        assertTrue(perfiles.autorizado(usuarios.findByLogin("cctprim").orElseThrow().getId(), "ESCUELA_DATOS"));
        assertTrue(perfiles.autorizado(usuarios.findByLogin("cctprim").orElseThrow().getId(), "USUARIOS_CAPTURAR"));
    }

    @Test
    void plantillaSeAplicaUnaVezYElPerfilSeReasigna() {
        var escuela = escuelas.altaPorCct("Plantillas", "15DPR0001Y", "FEDERAL",
                EnumSet.of(Modulo.ALUMNOS, Modulo.EVALUACION));
        var capturista = perfiles.plantillas().stream().filter(p -> "Capturista".equals(p.getNombre())).findFirst()
                .orElseThrow();
        var aplicado = perfiles.aplicarPlantilla(escuela.getId(), capturista.getId());
        assertEquals(aplicado.getId(), perfiles.aplicarPlantilla(escuela.getId(), capturista.getId()).getId());
        var docentePlantilla = perfiles.plantillas().stream().filter(p -> "Docente".equals(p.getNombre())).findFirst()
                .orElseThrow();
        var docente = perfiles.aplicarPlantilla(escuela.getId(), docentePlantilla.getId());
        var admin = perfiles.deLaEscuela(escuela.getId()).stream()
                .filter(p -> "Administrador escolar".equals(p.getNombre())).findFirst().orElseThrow();
        var usuario = perfiles.crearUsuarioPorSuper(escuela.getId(), "cambia", "clave", "Cambia", admin.getId(), false);
        perfiles.reasignar(escuela.getId(), usuario.getId(), docente.getId());
        assertEquals("Docente", perfiles.filas(escuela.getId()).stream()
                .filter(fila -> "cambia".equals(fila.usuario().getUsername())).findFirst().orElseThrow().perfilNombre());
        assertTrue(auditoria.deLaEscuela(escuela.getId()).stream()
                .anyMatch(e -> "REASIGNAR_PERFIL".equals(e.getAccion()) && "cambia".equals(e.getAfectadoLogin())));
    }

    @Test
    void superPuedeAltaExcepcionalConBitacora() {
        var escuela = escuelas.altaPorCct("Excepcional", "15PBH0001T", "PARTICULAR",
                EnumSet.of(Modulo.ALUMNOS, Modulo.EVALUACION));
        var docente = perfiles.crear(escuela.getId(), "Docente", Set.of("EVALUACION_CONSULTAR", "EVALUACION_CAPTURAR"));
        perfiles.crearUsuarioPorSuper(escuela.getId(), "profe1", "clave", "Profesor", docente.getId(), true);
        assertTrue(auditoria.deLaEscuela(escuela.getId()).stream()
                .anyMatch(e -> "ALTA_USUARIO_SUPER".equals(e.getAccion())
                        && e.getDetalle() != null && e.getDetalle().contains("excepcional")
                        && "profe1".equals(e.getAfectadoLogin())));
    }

    @Test
    void escuelaAsignaUsuarioConPerfilDelSuperYBitacora() {
        var escuela = escuelas.altaPorCct("Con perfiles", "15PBH0001T", "PARTICULAR",
                EnumSet.of(Modulo.ALUMNOS, Modulo.PLANTILLA), "adminperf", "clave");
        var perfil = perfiles.crear(escuela.getId(), "Capturista", Set.of("ALUMNOS_CONSULTAR", "ALUMNOS_CAPTURAR"));
        var usuario = perfiles.crearUsuario(escuela.getId(), "captura1", "clave", "Capturista Uno", perfil.getId());
        assertTrue(perfiles.autorizado(usuario.getId(), "ALUMNOS_CAPTURAR"));
        assertFalse(perfiles.autorizado(usuario.getId(), "ESCUELA_DATOS"));

        var eventos = auditoria.deLaEscuela(escuela.getId());
        assertTrue(eventos.stream().anyMatch(e -> "ALTA_ESCUELA".equals(e.getAccion())));
        assertTrue(eventos.stream().anyMatch(e -> "ALTA_PERFIL".equals(e.getAccion())));
        assertTrue(eventos.stream().anyMatch(e -> "ALTA_USUARIO".equals(e.getAccion())
                && "captura1".equals(e.getAfectadoLogin())));
    }

    @Test
    void crearUsuarioUsaPerfilDelSuperSinAltaDePerfilEscolar() {
        var escuela = escuelas.altaPorCct("Solo usuarios", "15DPR0001Y", "FEDERAL",
                EnumSet.of(Modulo.ALUMNOS), "solouser", "clave");
        assertEquals(1, perfiles.deLaEscuela(escuela.getId()).size());
        var perfil = perfiles.crear(escuela.getId(), "Capturista", Set.of("ALUMNOS_CONSULTAR"));
        assertEquals(2, perfiles.deLaEscuela(escuela.getId()).size());
        perfiles.crearUsuario(escuela.getId(), "u1", "clave", "Usuario Uno", perfil.getId());
        assertTrue(usuarios.findByLogin("u1").isPresent());
    }

    @Test
    @WithMockUser(roles = "SUPER")
    void superConsultaBitacora() throws Exception {
        var escuela = escuelas.altaPorCct("Bitacora", "15DPR0001Y", "FEDERAL",
                EnumSet.of(Modulo.ALUMNOS), "bitadmin", "clave");
        mvc.perform(get("/acceso/escuelas/" + escuela.getId() + "/bitacora"))
                .andExpect(status().isOk());
    }

    @Test
    void cuentaAlineadaConSpringSecurity() {
        var escuela = escuelas.altaPorCct("Seguridad", "15PBH0001T", "PARTICULAR",
                EnumSet.of(Modulo.ALUMNOS));
        var perfil = perfiles.deLaEscuela(escuela.getId()).get(0);
        var usuario = perfiles.crearUsuarioPorSuper(escuela.getId(), "secuser", "clave-inicial", "Seguridad",
                perfil.getId(), false);
        var cargado = (Usuario) detalle.loadUserByUsername("secuser");
        assertTrue(cargado.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ESCUELA")));
        assertTrue(cargado.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ALUMNOS_CONSULTAR")));
        assertTrue(cargado.isAccountNonLocked());
        assertTrue(cargado.isCredentialsNonExpired());

        acceso.bloquear(usuario.getId(), escuela.getId());
        assertFalse(usuarios.findByLogin("secuser").orElseThrow().isAccountNonLocked());
        acceso.desbloquear(usuario.getId(), escuela.getId());
        acceso.vencerCredencial(usuario.getId(), escuela.getId());
        assertFalse(usuarios.findByLogin("secuser").orElseThrow().isCredentialsNonExpired());
        acceso.reponerCredencial(usuario.getId(), escuela.getId(), "clave-nueva");
        var repuesto = usuarios.findByLogin("secuser").orElseThrow();
        assertTrue(repuesto.isCredentialsNonExpired());
        assertTrue(encoder.matches("clave-nueva", repuesto.getPassword()));
        repuesto.registrarIntentoFallido();
        repuesto.registrarIntentoFallido();
        repuesto.registrarIntentoFallido();
        repuesto.registrarIntentoFallido();
        repuesto.registrarIntentoFallido();
        assertFalse(repuesto.isAccountNonLocked());
    }
}
