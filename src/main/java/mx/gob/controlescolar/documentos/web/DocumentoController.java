package mx.gob.controlescolar.documentos.web;

import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.comun.web.SesionActual;
import mx.gob.controlescolar.documentos.aplicacion.ArchivoEscolar;
import mx.gob.controlescolar.documentos.aplicacion.DocumentoService;
import mx.gob.controlescolar.documentos.dominio.DocumentoEscolar;
import mx.gob.controlescolar.personas.aplicacion.AlumnoService;
import mx.gob.controlescolar.inscripcion.aplicacion.GrupoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentos;
    private final ArchivoEscolar archivos;
    private final AlumnoService alumnos;
    private final GrupoService grupos;
    private final PerfilService perfiles;
    private final SesionActual sesion;

    @GetMapping("/documentos")
    public String indice() {
        perfiles.exigir(sesion.usuario().getId(), "DOCUMENTOS_CONSULTAR");
        return "documentos/indice";
    }

    @GetMapping("/documentos/kardex")
    public String kardex(@RequestParam(required = false) Long grupoId, Model model) {
        return lista("Kardex", "historial", grupoId, model);
    }

    @GetMapping("/documentos/boletas")
    public String boletas(@RequestParam(required = false) Long grupoId, Model model) {
        return lista("Boleta", "boleta", grupoId, model);
    }

    @GetMapping("/documentos/constancias")
    public String constancias(@RequestParam(required = false) Long grupoId, Model model) {
        return lista("Constancia", "constancia", grupoId, model);
    }

    private String lista(String titulo, String emision, Long grupoId, Model model) {
        Long escuela = sesion.institucionId();
        perfiles.exigir(sesion.usuario().getId(), "DOCUMENTOS_CONSULTAR");
        var activas = alumnos.activas(escuela).stream()
                .filter(inscripcion -> grupoId == null || inscripcion.getGrupo().getId().equals(grupoId))
                .toList();
        model.addAttribute("titulo", titulo);
        model.addAttribute("emision", emision);
        model.addAttribute("grupos", grupos.consultar(escuela));
        model.addAttribute("grupoId", grupoId);
        model.addAttribute("inscripciones", activas);
        return "documentos/lista";
    }

    @GetMapping("/documentos/boleta")
    public ResponseEntity<byte[]> boleta(@RequestParam Long alumnoId, @RequestParam Long planVersionId) {
        return pdf("boleta.pdf", documentos.boleta(escuela(), alumnoId, planVersionId));
    }

    @GetMapping("/documentos/historial")
    public ResponseEntity<byte[]> historial(@RequestParam Long alumnoId, @RequestParam Long planVersionId) {
        return pdf("historial.pdf", documentos.historial(escuela(), alumnoId, planVersionId));
    }

    @GetMapping("/documentos/constancia")
    public ResponseEntity<byte[]> constancia(@RequestParam Long alumnoId, @RequestParam Long planVersionId) {
        return pdf("constancia.pdf", documentos.constancia(escuela(), alumnoId, planVersionId));
    }

    private Long escuela() {
        perfiles.exigir(sesion.usuario().getId(), "DOCUMENTOS_EMITIR");
        return sesion.institucionId();
    }

    private ResponseEntity<byte[]> pdf(String nombre, DocumentoEscolar documento) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + nombre)
                .contentType(MediaType.APPLICATION_PDF)
                .body(archivos.leer(documento.getArchivoId()));
    }
}
