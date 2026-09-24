package mx.gob.controlescolar.personas.web;

import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.comun.web.SesionActual;
import mx.gob.controlescolar.personas.aplicacion.DictamenService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class DictamenController {

    private final DictamenService dictamenes;
    private final PerfilService perfiles;
    private final SesionActual sesion;

    @GetMapping("/alumnos/{id}/dictamen")
    public String ver(@PathVariable Long id, Model model) {
        perfiles.exigir(sesion.usuario().getId(), "DICTAMEN_CONSULTAR");
        model.addAttribute("alumnoId", id);
        model.addAttribute("dictamenes", dictamenes.delAlumno(id));
        return "personas/dictamen";
    }

    @PostMapping("/alumnos/{id}/dictamen/revalidacion")
    public String revalidar(@PathVariable Long id, @RequestParam String dictamenPor, @RequestParam String escuelaOrigen,
                            @RequestParam String cicloInicio, @RequestParam String cicloFin,
                            @RequestParam(required = false) String estado, @RequestParam String pais,
                            @RequestParam(required = false) String expediente, @RequestParam(required = false) String folio,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                            @RequestParam(required = false) String materias) {
        perfiles.exigir(sesion.usuario().getId(), "DICTAMEN_CAPTURAR");
        dictamenes.revalidar(sesion.institucionId(), id, dictamenPor, escuelaOrigen, cicloInicio, cicloFin,
                estado, pais, expediente, folio, fecha, materias);
        return "redirect:/alumnos/" + id + "/dictamen";
    }

    @PostMapping("/alumnos/{id}/dictamen/equivalencia")
    public String equivalencia(@PathVariable Long id, @RequestParam String dictamenPor, @RequestParam String escuelaOrigen,
                               @RequestParam String cicloInicio, @RequestParam String cicloFin,
                               @RequestParam(required = false) String dependencia, @RequestParam String estado,
                               @RequestParam(required = false) String expediente, @RequestParam BigDecimal promedio,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                               @RequestParam(required = false) String materias) {
        perfiles.exigir(sesion.usuario().getId(), "DICTAMEN_CAPTURAR");
        dictamenes.equivaler(sesion.institucionId(), id, dictamenPor, escuelaOrigen, cicloInicio, cicloFin,
                dependencia, estado, expediente, promedio, fecha, materias);
        return "redirect:/alumnos/" + id + "/dictamen";
    }
}
