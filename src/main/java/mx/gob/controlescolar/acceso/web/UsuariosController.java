package mx.gob.controlescolar.acceso.web;

import mx.gob.controlescolar.acceso.aplicacion.AccesoService;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.comun.web.SesionActual;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

/**
 * Módulo USUARIOS de la escuela: consulta perfiles definidos por el super y alta de usuarios.
 */
@Controller
@RequiredArgsConstructor
public class UsuariosController {

    private final PerfilService perfiles;
    private final AccesoService acceso;
    private final SesionActual sesion;

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        Long escuela = sesion.institucionId();
        perfiles.exigir(sesion.usuario().getId(), "USUARIOS_CONSULTAR");
        model.addAttribute("perfiles", perfiles.deLaEscuela(escuela));
        model.addAttribute("usuarios", perfiles.filas(escuela));
        model.addAttribute("puedeCapturar", perfiles.autorizado(sesion.usuario().getId(), "USUARIOS_CAPTURAR"));
        return "acceso/usuarios-escuela";
    }

    @PostMapping("/usuarios")
    public String crear(@RequestParam String login, @RequestParam String clave, @RequestParam String nombre,
                        @RequestParam Long perfilId) {
        perfiles.exigir(sesion.usuario().getId(), "USUARIOS_CAPTURAR");
        perfiles.crearUsuario(sesion.institucionId(), login, clave, nombre, perfilId);
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/{id}")
    public String cuenta(@PathVariable Long id, Model model) {
        Long escuela = sesion.institucionId();
        perfiles.exigir(sesion.usuario().getId(), "USUARIOS_CONSULTAR");
        var fila = perfiles.filas(escuela).stream().filter(item -> item.usuario().getId().equals(id)).findFirst()
                .orElseThrow(() -> new mx.gob.controlescolar.comun.aplicacion.NegocioException("El usuario no existe"));
        model.addAttribute("fila", fila);
        model.addAttribute("perfiles", perfiles.deLaEscuela(escuela));
        model.addAttribute("puedeCapturar", perfiles.autorizado(sesion.usuario().getId(), "USUARIOS_CAPTURAR"));
        model.addAttribute("escuelaId", escuela);
        model.addAttribute("superusuario", false);
        return "acceso/cuenta";
    }

    @PostMapping("/usuarios/perfil")
    public String reasignar(@RequestParam Long usuarioId, @RequestParam Long perfilId) {
        perfiles.exigir(sesion.usuario().getId(), "USUARIOS_CAPTURAR");
        perfiles.reasignar(sesion.institucionId(), usuarioId, perfilId);
        return cuentaDe(usuarioId);
    }

    @PostMapping("/usuarios/bloquear")
    public String bloquear(@RequestParam Long usuarioId) {
        perfiles.exigir(sesion.usuario().getId(), "USUARIOS_CAPTURAR");
        acceso.bloquear(usuarioId, sesion.institucionId());
        return cuentaDe(usuarioId);
    }

    @PostMapping("/usuarios/desbloquear")
    public String desbloquear(@RequestParam Long usuarioId) {
        perfiles.exigir(sesion.usuario().getId(), "USUARIOS_CAPTURAR");
        acceso.desbloquear(usuarioId, sesion.institucionId());
        return cuentaDe(usuarioId);
    }

    @PostMapping("/usuarios/credencial")
    public String credencial(@RequestParam Long usuarioId, @RequestParam String clave) {
        perfiles.exigir(sesion.usuario().getId(), "USUARIOS_CAPTURAR");
        acceso.reponerCredencial(usuarioId, sesion.institucionId(), clave);
        return cuentaDe(usuarioId);
    }

    private static String cuentaDe(Long usuarioId) {
        return "redirect:/usuarios/" + usuarioId;
    }

    @GetMapping("/perfiles")
    public String perfiles(Model model) {
        Long escuela = sesion.institucionId();
        perfiles.exigir(sesion.usuario().getId(), "USUARIOS_CONSULTAR");
        model.addAttribute("perfiles", perfiles.deLaEscuela(escuela));
        return "acceso/perfiles-consulta";
    }
}
