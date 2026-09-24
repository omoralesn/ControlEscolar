package mx.gob.controlescolar.asistencia.web;

import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.asistencia.aplicacion.AsistenciaService;
import mx.gob.controlescolar.comun.web.SesionActual;
import mx.gob.controlescolar.inscripcion.aplicacion.GrupoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistencia;
    private final GrupoService grupos;
    private final PerfilService perfiles;
    private final ModuloGuardia modulos;
    private final SesionActual sesion;

    @GetMapping("/asistencia")
    public String formulario(@RequestParam(required = false) Long alumnoId,
                             @RequestParam(required = false) Long profesorId, Model model) {
        Long escuela = sesion.institucionId();
        model.addAttribute("grupos", grupos.consultar(escuela));
        model.addAttribute("profesores", modulos.activo(escuela, Modulo.PLANTILLA)
                ? grupos.plantilla(escuela) : java.util.List.of());
        if (alumnoId != null) {
            model.addAttribute("faltasAlumno", asistencia.faltasAlumno(alumnoId));
            model.addAttribute("historicoAlumno", asistencia.historicoAlumno(alumnoId));
        }
        if (profesorId != null) {
            model.addAttribute("faltasProfesor", asistencia.faltasProfesor(escuela, profesorId));
            model.addAttribute("historicoProfesor", asistencia.historicoProfesor(escuela, profesorId));
        }
        return "asistencia/listas";
    }

    @PostMapping("/asistencia")
    public String lista(@RequestParam Long grupoId, @RequestParam LocalDate fecha, @RequestParam Long alumnoId,
                        @RequestParam(defaultValue = "false") boolean presente) {
        perfiles.exigir(sesion.usuario().getId(), "EVALUACION_CAPTURAR");
        asistencia.registrarLista(sesion.institucionId(), grupoId, fecha, alumnoId, presente);
        return "redirect:/asistencia?alumnoId=" + alumnoId;
    }

    @PostMapping("/asistencia/profesor")
    public String profesor(@RequestParam Long profesorId, @RequestParam LocalDate fecha) {
        perfiles.exigir(sesion.usuario().getId(), "PLANTILLA_CAPTURAR");
        asistencia.registrarFaltaProfesor(sesion.institucionId(), profesorId, fecha);
        return "redirect:/asistencia?profesorId=" + profesorId;
    }
}
