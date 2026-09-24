package mx.gob.controlescolar.comun.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChecklistController {

    @GetMapping("/checklist")
    public String checklist() {
        return "comun/checklist";
    }
}
