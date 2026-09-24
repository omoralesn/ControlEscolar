package mx.gob.controlescolar.inscripcion.web;

import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.academico.aplicacion.ProgramaService;
import mx.gob.controlescolar.comun.web.SesionActual;
import mx.gob.controlescolar.inscripcion.aplicacion.GrupoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoService grupos;
    private final ProgramaService programas;
    private final PerfilService perfiles;
    private final ModuloGuardia modulos;
    private final SesionActual sesion;

    @GetMapping("/grupos")
    public String listar(Model model) {
        Long escuela = sesion.institucionId();
        model.addAttribute("grupos", grupos.consultar(escuela));
        model.addAttribute("programas", programas.deLaEscuela(escuela));
        model.addAttribute("profesores", modulos.activo(escuela, Modulo.PLANTILLA)
                ? grupos.plantilla(escuela) : java.util.List.of());
        model.addAttribute("horarios", modulos.activo(escuela, Modulo.PLANTILLA)
                ? grupos.horarios(escuela) : java.util.List.of());
        return "inscripcion/grupos";
    }

    @PostMapping("/grupos")
    public String registrar(@RequestParam Long programaId, @RequestParam String nombre, @RequestParam int periodoOrden) {
        perfiles.exigir(sesion.usuario().getId(), "INSCRIPCION_CAPTURAR");
        grupos.registrar(sesion.institucionId(), programaId, nombre, periodoOrden);
        return "redirect:/grupos";
    }

    @PostMapping("/profesores")
    public String profesor(@RequestParam String nombre) {
        perfiles.exigir(sesion.usuario().getId(), "PLANTILLA_CAPTURAR");
        grupos.registrarProfesor(sesion.institucionId(), nombre);
        return "redirect:/grupos";
    }

    @PostMapping("/horarios")
    public String horario(@RequestParam Long grupoId, @RequestParam Long profesorId, @RequestParam String asignatura,
                          @RequestParam String dia, @RequestParam String horaInicio, @RequestParam String horaFin) {
        perfiles.exigir(sesion.usuario().getId(), "PLANTILLA_CAPTURAR");
        grupos.asignar(sesion.institucionId(), grupoId, profesorId, asignatura, dia, horaInicio, horaFin);
        return "redirect:/grupos";
    }
}
