package mx.gob.controlescolar.comun.web;

import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(NegocioException.class)
    public String negocio(NegocioException error, Model model) {
        model.addAttribute("mensaje", error.getMessage());
        return "comun/error";
    }

    @ExceptionHandler(Exception.class)
    public String error(Model model) {
        model.addAttribute("mensaje", "No se pudo completar la operación.");
        return "comun/error";
    }
}
