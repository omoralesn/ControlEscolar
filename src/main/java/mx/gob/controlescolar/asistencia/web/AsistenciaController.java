package mx.gob.controlescolar.asistencia.web;

import jakarta.servlet.http.HttpServletRequest;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.asistencia.aplicacion.AsistenciaService;
import mx.gob.controlescolar.comun.web.SesionActual;
import mx.gob.controlescolar.inscripcion.aplicacion.GrupoService;
import mx.gob.controlescolar.personas.aplicacion.AlumnoService;
import mx.gob.controlescolar.personas.dominio.Inscripcion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistencia;
    private final GrupoService grupos;
    private final AlumnoService alumnos;
    private final PerfilService perfiles;
    private final ModuloGuardia modulos;
    private final SesionActual sesion;

    @GetMapping("/asistencia")
    public String formulario(@RequestParam(required = false) Long grupoId,
                             @RequestParam(required = false) LocalDate fecha,
                             @RequestParam(required = false) Long profesorId, Model model) {
        Long escuela = sesion.institucionId();
        LocalDate dia = fecha == null ? LocalDate.now() : fecha;
        model.addAttribute("grupos", grupos.consultar(escuela));
        model.addAttribute("grupoId", grupoId);
        model.addAttribute("fecha", dia);
        model.addAttribute("profesores", modulos.activo(escuela, Modulo.PLANTILLA)
                ? grupos.plantilla(escuela) : java.util.List.of());
        if (grupoId != null) {
            List<Inscripcion> filas = alumnos.activas(escuela).stream()
                    .filter(inscripcion -> inscripcion.getGrupo().getId().equals(grupoId))
                    .toList();
            model.addAttribute("filas", filas);
            model.addAttribute("marcas", asistencia.deLaLista(escuela, grupoId, dia));
        }
        if (profesorId != null) {
            model.addAttribute("faltasProfesor", asistencia.faltasProfesor(escuela, profesorId));
            model.addAttribute("profesorId", profesorId);
        }
        return "asistencia/listas";
    }

    @PostMapping("/asistencia")
    public String matriz(@RequestParam Long grupoId, @RequestParam LocalDate fecha, HttpServletRequest solicitud) {
        perfiles.exigir(sesion.usuario().getId(), "EVALUACION_CAPTURAR");
        Map<Long, Boolean> presentes = new LinkedHashMap<>();
        String[] ids = solicitud.getParameterValues("alumnoId");
        if (ids != null) {
            for (String id : ids) {
                Long alumnoId = Long.valueOf(id);
                presentes.put(alumnoId, solicitud.getParameter("p_" + alumnoId) != null);
            }
        }
        asistencia.registrarMatriz(sesion.institucionId(), grupoId, fecha, presentes);
        solicitud.getSession().setAttribute("aviso", "Lista del " + fecha + " guardada.");
        return "redirect:/asistencia?grupoId=" + grupoId + "&fecha=" + fecha;
    }

    @PostMapping("/asistencia/profesor")
    public String profesor(@RequestParam Long profesorId, @RequestParam LocalDate fecha) {
        perfiles.exigir(sesion.usuario().getId(), "PLANTILLA_CAPTURAR");
        asistencia.registrarFaltaProfesor(sesion.institucionId(), profesorId, fecha);
        return "redirect:/asistencia?profesorId=" + profesorId;
    }
}
