package mx.gob.controlescolar.acceso.web;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

import mx.gob.controlescolar.acceso.aplicacion.AccesoService;
import mx.gob.controlescolar.acceso.aplicacion.AuditoriaService;
import mx.gob.controlescolar.acceso.aplicacion.CentroTrabajoService;
import mx.gob.controlescolar.acceso.aplicacion.EscuelaService;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.acceso.dominio.CatalogoPaginas;
import mx.gob.controlescolar.acceso.dominio.ClaveCentroTrabajo;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccesoController {

    private final EscuelaService escuelas;
    private final CentroTrabajoService centros;
    private final AccesoService acceso;
    private final PerfilService perfiles;
    private final ModuloGuardia modulos;
    private final AuditoriaService auditoria;

    @GetMapping("/acceso")
    public String indice(Model model) {
        model.addAttribute("escuelas", escuelas.listar());
        model.addAttribute("planteles", acceso.planteles());
        return "acceso/indice";
    }

    @GetMapping("/acceso/escuelas")
    public String escuelas(Model model) {
        model.addAttribute("escuelas", escuelas.listar());
        model.addAttribute("modulos", Modulo.values());
        return "acceso/alta";
    }

    @GetMapping("/acceso/usuarios")
    public String moduloUsuarios(Model model) {
        model.addAttribute("escuelas", escuelas.listar());
        return "acceso/usuarios-indice";
    }

    @GetMapping("/acceso/perfiles")
    public String moduloPerfiles(Model model) {
        model.addAttribute("plantillas", perfiles.plantillas());
        model.addAttribute("permisos", mx.gob.controlescolar.acceso.dominio.PermisoCatalogo.TODOS);
        model.addAttribute("escuelas", escuelas.listar());
        return "acceso/perfiles-indice";
    }

    @GetMapping("/acceso/planteles")
    public String planteles(Model model) {
        model.addAttribute("planteles", acceso.planteles());
        return "acceso/planteles";
    }

    @GetMapping("/acceso/plantilla")
    public String plantilla(Model model) {
        model.addAttribute("paginas", CatalogoPaginas.TODAS);
        return "acceso/plantilla";
    }

    @GetMapping("/acceso/escuelas/{id}")
    public String escuela(@PathVariable Long id, Model model) {
        Institucion escuela = escuelas.obtener(id);
        model.addAttribute("escuela", escuela);
        model.addAttribute("escuelaId", id);
        model.addAttribute("centros", centros.deLaEscuela(id));
        return "acceso/escuela";
    }

    @GetMapping("/acceso/escuelas/{id}/cct")
    public String cct(@PathVariable Long id, Model model) {
        model.addAttribute("escuela", escuelas.obtener(id));
        model.addAttribute("escuelaId", id);
        model.addAttribute("centros", centros.deLaEscuela(id));
        return "acceso/cct";
    }

    @GetMapping("/acceso/escuelas/{id}/modulos")
    public String modulos(@PathVariable Long id, Model model) {
        model.addAttribute("escuela", escuelas.obtener(id));
        model.addAttribute("escuelaId", id);
        model.addAttribute("modulos", Modulo.values());
        model.addAttribute("modulosActivos", modulos.activos(id));
        return "acceso/modulos";
    }

    @GetMapping("/acceso/escuelas/{id}/usuarios")
    public String usuarios(@PathVariable Long id, Model model) {
        model.addAttribute("escuela", escuelas.obtener(id));
        model.addAttribute("escuelaId", id);
        model.addAttribute("usuarios", perfiles.filas(id));
        model.addAttribute("perfiles", perfiles.deLaEscuela(id));
        model.addAttribute("plantillas", perfiles.plantillas());
        return "acceso/usuarios";
    }

    @GetMapping("/acceso/escuelas/{escuelaId}/cuentas/{usuarioId}")
    public String cuenta(@PathVariable Long escuelaId, @PathVariable Long usuarioId, Model model) {
        var fila = perfiles.filas(escuelaId).stream().filter(item -> item.usuario().getId().equals(usuarioId)).findFirst()
                .orElseThrow(() -> new mx.gob.controlescolar.comun.aplicacion.NegocioException("El usuario no existe"));
        model.addAttribute("fila", fila);
        model.addAttribute("perfiles", perfiles.deLaEscuela(escuelaId));
        model.addAttribute("escuelaId", escuelaId);
        model.addAttribute("escuela", escuelas.obtener(escuelaId));
        model.addAttribute("puedeCapturar", true);
        model.addAttribute("superusuario", true);
        return "acceso/cuenta";
    }

    @GetMapping("/acceso/escuelas/{id}/perfiles")
    public String perfiles(@PathVariable Long id, Model model) {
        model.addAttribute("escuela", escuelas.obtener(id));
        model.addAttribute("escuelaId", id);
        model.addAttribute("perfiles", perfiles.deLaEscuela(id));
        model.addAttribute("disponibles", perfiles.disponibles(id));
        model.addAttribute("plantillas", perfiles.plantillas());
        return "acceso/perfiles-escuela";
    }

    @GetMapping("/acceso/escuelas/{id}/bitacora")
    public String bitacora(@PathVariable Long id, Model model) {
        model.addAttribute("escuela", escuelas.obtener(id));
        model.addAttribute("escuelaId", id);
        model.addAttribute("eventos", auditoria.deLaEscuela(id));
        return "acceso/bitacora";
    }

    /** Paso 1: analiza la CCT y muestra entidad, nivel, sostenimiento y progresivo para revisión. */
    @PostMapping("/acceso/escuelas/revisar")
    public String revisar(@RequestParam String nombre,
                          @RequestParam String claveCct,
                          @RequestParam(required = false) String sostenimiento,
                          @RequestParam(required = false) Set<Modulo> habilitados,
                          Model model) {
        ClaveCentroTrabajo clave = ClaveCentroTrabajo.analizar(claveCct, sostenimiento);
        Set<Modulo> activos = habilitados == null ? EnumSet.noneOf(Modulo.class) : EnumSet.copyOf(habilitados);
        model.addAttribute("nombre", nombre);
        model.addAttribute("claveCct", clave.texto());
        model.addAttribute("sostenimiento", clave.sostenimiento());
        model.addAttribute("clave", clave);
        model.addAttribute("habilitados", activos);
        model.addAttribute("modulos", Modulo.values());
        return "acceso/alta-revisar";
    }

    @PostMapping("/acceso/escuelas")
    public String confirmar(@RequestParam String nombre,
                            @RequestParam String claveCct,
                            @RequestParam(required = false) String sostenimiento,
                            @RequestParam(required = false) Set<Modulo> habilitados) {
        Set<Modulo> activos = habilitados == null ? EnumSet.noneOf(Modulo.class) : EnumSet.copyOf(habilitados);
        Institucion escuela = escuelas.altaPorCct(nombre, claveCct, sostenimiento, activos);
        return "redirect:/acceso/escuelas/" + escuela.getId() + "/usuarios";
    }

    @PostMapping("/acceso/usuarios")
    public String altaUsuario(@RequestParam Long escuelaId, @RequestParam String login, @RequestParam String clave,
                              @RequestParam String nombre, @RequestParam Long perfilId,
                              @RequestParam(required = false) Boolean excepcional) {
        perfiles.crearUsuarioPorSuper(escuelaId, login, clave, nombre, perfilId, Boolean.TRUE.equals(excepcional));
        return "redirect:/acceso/escuelas/" + escuelaId + "/usuarios";
    }

    @PostMapping("/acceso/usuarios/perfil")
    public String reasignar(@RequestParam Long escuelaId, @RequestParam Long usuarioId, @RequestParam Long perfilId) {
        perfiles.reasignar(escuelaId, usuarioId, perfilId);
        return "redirect:/acceso/escuelas/" + escuelaId + "/cuentas/" + usuarioId;
    }

    @PostMapping("/acceso/perfiles/plantilla")
    public String plantillaPerfil(@RequestParam String nombre, @RequestParam(required = false) Set<String> codigos) {
        perfiles.crearPlantilla(nombre, codigos == null ? Set.of() : codigos);
        return "redirect:/acceso/perfiles";
    }

    @PostMapping("/acceso/perfiles/aplicar")
    public String aplicarPlantilla(@RequestParam Long escuelaId, @RequestParam Long plantillaId) {
        perfiles.aplicarPlantilla(escuelaId, plantillaId);
        return "redirect:/acceso/escuelas/" + escuelaId + "/perfiles";
    }

    @PostMapping("/acceso/cct")
    public String asignarCct(@RequestParam Long institucionId, @RequestParam String clave,
                             @RequestParam(required = false) String sostenimiento, @RequestParam String nombre) {
        String cct = clave == null ? null : clave.trim().toUpperCase();
        centros.registrar(institucionId, cct, sostenimiento, nombre);
        return "redirect:/acceso/escuelas/" + institucionId + "/cct";
    }

    @PostMapping("/acceso/modulos")
    public String guardarModulos(@RequestParam Long institucionId, @RequestParam(required = false) Set<Modulo> habilitados) {
        escuelas.definirModulos(institucionId, habilitados == null ? EnumSet.noneOf(Modulo.class) : EnumSet.copyOf(habilitados));
        return "redirect:/acceso/escuelas/" + institucionId + "/modulos";
    }

    @PostMapping("/acceso/escuelas/{id}/suspender")
    public String suspenderEscuela(@PathVariable Long id) {
        escuelas.suspender(id);
        return "redirect:/acceso/escuelas";
    }

    @PostMapping("/acceso/escuelas/{id}/activar")
    public String activarEscuela(@PathVariable Long id) {
        escuelas.activar(id);
        return "redirect:/acceso/escuelas";
    }

    @PostMapping("/acceso/planteles/{id}/suspender")
    public String suspenderPlantel(@PathVariable Long id) {
        acceso.suspenderPlantel(id);
        return "redirect:/acceso/planteles";
    }

    @PostMapping("/acceso/planteles/{id}/activar")
    public String activarPlantel(@PathVariable Long id) {
        acceso.activarPlantel(id);
        return "redirect:/acceso/planteles";
    }

    @PostMapping("/acceso/usuarios/vigencia")
    public String vigencia(@RequestParam Long usuarioId, @RequestParam Long escuelaId,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vigenteDesde,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vigenteHasta) {
        acceso.definirVigencia(usuarioId, vigenteDesde, vigenteHasta);
        return "redirect:/acceso/escuelas/" + escuelaId + "/cuentas/" + usuarioId;
    }

    @PostMapping("/acceso/usuarios/{id}/suspender")
    public String suspenderUsuario(@PathVariable Long id, @RequestParam Long escuelaId) {
        acceso.suspenderUsuario(id);
        return "redirect:/acceso/escuelas/" + escuelaId + "/cuentas/" + id;
    }

    @PostMapping("/acceso/usuarios/{id}/activar")
    public String activarUsuario(@PathVariable Long id, @RequestParam Long escuelaId) {
        acceso.activarUsuario(id);
        return "redirect:/acceso/escuelas/" + escuelaId + "/cuentas/" + id;
    }

    @PostMapping("/acceso/usuarios/{id}/bloquear")
    public String bloquearUsuario(@PathVariable Long id, @RequestParam Long escuelaId) {
        acceso.bloquear(id, escuelaId);
        return "redirect:/acceso/escuelas/" + escuelaId + "/cuentas/" + id;
    }

    @PostMapping("/acceso/usuarios/{id}/desbloquear")
    public String desbloquearUsuario(@PathVariable Long id, @RequestParam Long escuelaId) {
        acceso.desbloquear(id, escuelaId);
        return "redirect:/acceso/escuelas/" + escuelaId + "/cuentas/" + id;
    }

    @PostMapping("/acceso/usuarios/credencial")
    public String credencial(@RequestParam Long usuarioId, @RequestParam Long escuelaId, @RequestParam String clave) {
        acceso.reponerCredencial(usuarioId, escuelaId, clave);
        return "redirect:/acceso/escuelas/" + escuelaId + "/cuentas/" + usuarioId;
    }

    @PostMapping("/acceso/perfiles")
    public String perfil(@RequestParam Long escuelaId, @RequestParam String nombre,
                         @RequestParam(required = false) Set<String> codigos) {
        perfiles.crear(escuelaId, nombre, codigos == null ? Set.of() : codigos);
        return "redirect:/acceso/escuelas/" + escuelaId + "/perfiles";
    }
}
