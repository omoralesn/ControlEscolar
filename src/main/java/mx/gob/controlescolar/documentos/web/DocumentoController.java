package mx.gob.controlescolar.documentos.web;

import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.comun.web.SesionActual;
import mx.gob.controlescolar.documentos.aplicacion.ArchivoEscolar;
import mx.gob.controlescolar.documentos.aplicacion.DocumentoService;
import mx.gob.controlescolar.documentos.dominio.DocumentoEscolar;
import mx.gob.controlescolar.personas.aplicacion.AlumnoService;
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
    private final PerfilService perfiles;
    private final SesionActual sesion;

    @GetMapping("/documentos")
    public String listar(Model model) {
        Long escuela = sesion.institucionId();
        perfiles.exigir(sesion.usuario().getId(), "DOCUMENTOS_CONSULTAR");
        model.addAttribute("inscripciones", alumnos.activas(escuela));
        return "documentos/documentos";
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
