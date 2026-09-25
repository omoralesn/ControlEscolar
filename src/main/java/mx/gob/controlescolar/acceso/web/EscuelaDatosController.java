package mx.gob.controlescolar.acceso.web;

import mx.gob.controlescolar.acceso.aplicacion.EscuelaService;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.comun.web.SesionActual;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EscuelaDatosController {

    private final EscuelaService escuelas;
    private final PerfilService perfiles;
    private final SesionActual sesion;

    @GetMapping("/escuela/datos")
    public String ver(@RequestParam(required = false) String cp, Model model) {
        Long id = sesion.institucionId();
        model.addAttribute("escuela", escuelas.obtener(id));
        model.addAttribute("cp", cp);
        model.addAttribute("localidadesCp", escuelas.localidadesPorCp(cp));
        model.addAttribute("puedeEditar", perfiles.autorizado(sesion.usuario().getId(), "ESCUELA_DATOS"));
        return "acceso/datos-escuela";
    }

    @PostMapping("/escuela/datos/domicilio")
    public String domicilio(@RequestParam(required = false) String calle,
                            @RequestParam(required = false) String numeroExterior,
                            @RequestParam(required = false) String numeroInterior,
                            @RequestParam(required = false) String colonia,
                            @RequestParam(required = false) String codigoPostal,
                            @RequestParam(required = false) String referencia,
                            @RequestParam(required = false) Long localidadId) {
        perfiles.exigir(sesion.usuario().getId(), "ESCUELA_DATOS");
        escuelas.actualizarDomicilio(sesion.institucionId(),
                new EscuelaService.DatosDomicilio(calle, numeroExterior, numeroInterior, colonia, codigoPostal,
                        referencia, localidadId));
        return "redirect:/escuela/datos";
    }

    @PostMapping("/escuela/datos/contacto")
    public String contacto(@RequestParam(required = false) String contactoNombre,
                           @RequestParam(required = false) String telefono,
                           @RequestParam(required = false) String celular,
                           @RequestParam(required = false) String correo) {
        perfiles.exigir(sesion.usuario().getId(), "ESCUELA_DATOS");
        escuelas.actualizarContacto(sesion.institucionId(),
                new EscuelaService.DatosContacto(contactoNombre, telefono, celular, correo));
        return "redirect:/escuela/datos";
    }
}
