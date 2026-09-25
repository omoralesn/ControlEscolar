package mx.gob.controlescolar.personas.web;

import java.time.LocalDate;
import java.util.Set;
import lombok.Generated;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.comun.persistencia.EstadoRepositorio;
import mx.gob.controlescolar.comun.persistencia.LocalidadRepositorio;
import mx.gob.controlescolar.comun.persistencia.MunicipioRepositorio;
import mx.gob.controlescolar.comun.web.SesionActual;
import mx.gob.controlescolar.personas.aplicacion.Apoyo911Service;
import mx.gob.controlescolar.personas.aplicacion.ExpedienteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ExpedienteController {
    private final ExpedienteService expedientes;
    private final Apoyo911Service apoyo911;
    private final PerfilService perfiles;
    private final SesionActual sesion;
    private final EstadoRepositorio estados;
    private final MunicipioRepositorio municipios;
    private final LocalidadRepositorio localidades;
    private final mx.gob.controlescolar.personas.aplicacion.AccesoTutorService accesosTutor;

    @GetMapping(value={"/alumnos/{id}/expediente"})
    public String ver(@PathVariable Long id, Model model) {
        Long escuela = this.sesion.institucionId();
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CONSULTAR");
        ExpedienteService.Expediente expediente = this.expedientes.consultar(escuela, id);
        model.addAttribute("expediente", (Object)expediente);
        model.addAttribute("discapacidades", this.expedientes.catalogoDiscapacidades());
        model.addAttribute("estados", (Object)this.estados.findAll());
        model.addAttribute("municipios", (Object)this.municipios.findAll());
        model.addAttribute("localidades", (Object)this.localidades.findAll());
        return "personas/expediente";
    }

    @PostMapping(value={"/alumnos/{id}/expediente/datos"})
    public String datos(@PathVariable Long id, @RequestParam String nombre, @RequestParam String apellidoPaterno, @RequestParam(required=false) String apellidoMaterno, @RequestParam(required=false) String sexo, @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento, @RequestParam(defaultValue="false") boolean usaLentes, @RequestParam(defaultValue="false") boolean usaZapatoOrtopedico, @RequestParam(required=false) Set<Long> discapacidadIds) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CAPTURAR");
        this.expedientes.guardarDatos(this.sesion.institucionId(), id, nombre, apellidoPaterno, apellidoMaterno, sexo, fechaNacimiento, usaLentes, usaZapatoOrtopedico, discapacidadIds);
        return "redirect:/alumnos/" + id + "/expediente";
    }

    @PostMapping(value={"/alumnos/{id}/expediente/domicilio"})
    public String domicilio(@PathVariable Long id, @RequestParam(required=false) String calle, @RequestParam(required=false) String numeroExterior, @RequestParam(required=false) String numeroInterior, @RequestParam(required=false) String entreCalle, @RequestParam(required=false) String yCalle, @RequestParam(required=false) String colonia, @RequestParam(required=false) String codigoPostal, @RequestParam(required=false) String referencia, @RequestParam(required=false) Long estadoId, @RequestParam(required=false) Long municipioId, @RequestParam(required=false) Long localidadId) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CAPTURAR");
        this.expedientes.guardarDomicilioAlumno(this.sesion.institucionId(), id, new ExpedienteService.DatosDomicilio(calle, numeroExterior, numeroInterior, entreCalle, yCalle, colonia, codigoPostal, referencia, estadoId, municipioId, localidadId));
        return "redirect:/alumnos/" + id + "/expediente";
    }

    @PostMapping(value={"/alumnos/{id}/expediente/responsable"})
    public String responsable(@PathVariable Long id, @RequestParam(required=false) String curp, @RequestParam String nombre, @RequestParam(required=false) String apellidoPaterno, @RequestParam(required=false) String apellidoMaterno, @RequestParam(required=false) String parentesco, @RequestParam(required=false) String telefono, @RequestParam(required=false) String celular, @RequestParam(required=false) String correo, @RequestParam(required=false) String estudios, @RequestParam(required=false) String ocupacion, @RequestParam(defaultValue="false") boolean viveConAlumno, @RequestParam(required=false) String calle, @RequestParam(required=false) String numeroExterior, @RequestParam(required=false) String colonia, @RequestParam(required=false) String codigoPostal, @RequestParam(required=false) Long estadoId, @RequestParam(required=false) Long municipioId, @RequestParam(required=false) Long localidadId) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CAPTURAR");
        this.expedientes.guardarResponsable(this.sesion.institucionId(), id, new ExpedienteService.DatosResponsable(curp, nombre, apellidoPaterno, apellidoMaterno, parentesco, telefono, celular, correo, estudios, ocupacion, viveConAlumno, new ExpedienteService.DatosDomicilio(calle, numeroExterior, null, null, null, colonia, codigoPostal, null, estadoId, municipioId, localidadId)));
        return "redirect:/alumnos/" + id + "/expediente";
    }

    @GetMapping(value={"/estadistica/911"})
    public String apoyo911(Model model) {
        Long escuela = this.sesion.institucionId();
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CONSULTAR");
        model.addAttribute("resumen", (Object)this.apoyo911.resumen(escuela));
        return "personas/apoyo911";
    }

    @PostMapping("/alumnos/{id}/acceso-tutor")
    public String accesoTutor(@PathVariable Long id, @RequestParam(required = false) String claveTutor,
                              org.springframework.web.servlet.mvc.support.RedirectAttributes redirect) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CAPTURAR");
        var acceso = this.accesosTutor.definir(this.sesion.institucionId(), id, claveTutor);
        redirect.addFlashAttribute("claveTutor", acceso.clave());
        redirect.addFlashAttribute("matriculaTutor", acceso.matricula());
        return "redirect:/alumnos/" + id + "/expediente";
    }

    @Generated
    public ExpedienteController(ExpedienteService expedientes, Apoyo911Service apoyo911, PerfilService perfiles, SesionActual sesion, EstadoRepositorio estados, MunicipioRepositorio municipios, LocalidadRepositorio localidades, mx.gob.controlescolar.personas.aplicacion.AccesoTutorService accesosTutor) {
        this.expedientes = expedientes;
        this.apoyo911 = apoyo911;
        this.perfiles = perfiles;
        this.sesion = sesion;
        this.estados = estados;
        this.municipios = municipios;
        this.localidades = localidades;
        this.accesosTutor = accesosTutor;
    }
}
