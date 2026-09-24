package mx.gob.controlescolar.acceso.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EntradaController {

    @GetMapping("/entrar")
    public String entrar() {
        return "acceso/entrar";
    }
}
