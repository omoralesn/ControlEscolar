package mx.gob.controlescolar.documentos.aplicacion;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import lombok.Generated;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.persistencia.InstitucionRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.documentos.aplicacion.ArchivoEscolar;
import mx.gob.controlescolar.documentos.dominio.DocumentoEscolar;
import mx.gob.controlescolar.documentos.persistencia.DocumentoRepositorio;
import mx.gob.controlescolar.evaluacion.dominio.Calificacion;
import mx.gob.controlescolar.evaluacion.persistencia.CalificacionRepositorio;
import mx.gob.controlescolar.personas.dominio.Alumno;
import mx.gob.controlescolar.personas.dominio.Dictamen;
import mx.gob.controlescolar.personas.persistencia.AlumnoRepositorio;
import mx.gob.controlescolar.personas.persistencia.DictamenRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentoService {
    private final DocumentoRepositorio documentos;
    private final AlumnoRepositorio alumnos;
    private final InstitucionRepositorio instituciones;
    private final CalificacionRepositorio calificaciones;
    private final DictamenRepositorio dictamenes;
    private final ArchivoEscolar archivos;
    private final ModuloGuardia modulos;

    @Transactional
    public DocumentoEscolar constancia(Long institucionId, Long alumnoId, Long planVersionId) {
        return this.emitir(institucionId, alumnoId, planVersionId, "CONSTANCIA", false);
    }

    @Transactional
    public DocumentoEscolar boleta(Long institucionId, Long alumnoId, Long planVersionId) {
        return this.emitir(institucionId, alumnoId, planVersionId, "BOLETA", true);
    }

    @Transactional
    public DocumentoEscolar historial(Long institucionId, Long alumnoId, Long planVersionId) {
        return this.emitir(institucionId, alumnoId, planVersionId, "HISTORIAL", false);
    }

    public String texto(Long institucionId, Long alumnoId, Long planVersionId, boolean incluyeAnexo) {
        Institucion institucion = (Institucion)this.instituciones.findById(institucionId).orElseThrow();
        Alumno alumno = (Alumno)this.alumnos.findById(alumnoId).orElseThrow();
        StringBuilder texto = new StringBuilder();
        texto.append(institucion.getNombre()).append('\n');
        texto.append(alumno.nombreCompleto()).append('\n');
        for (Calificacion calificacion : this.calificaciones.findByAlumnoIdAndPlanVersionId(alumnoId, planVersionId)) {
            if (!incluyeAnexo && calificacion.isComplementaria()) continue;
            texto.append(calificacion.getAsignaturaClave()).append(' ').append(calificacion.getValor() == null ? calificacion.getObservacion() : calificacion.getValor()).append('\n');
        }
        for (Dictamen dictamen : this.dictamenes.findByAlumnoId(alumnoId)) {
            texto.append(dictamen.getTipo()).append(' ').append(dictamen.getEscuelaOrigen());
            if (dictamen.getMaterias() != null && !dictamen.getMaterias().isBlank()) {
                texto.append(' ').append(dictamen.getMaterias());
            }
            texto.append('\n');
        }
        return texto.toString();
    }

    private DocumentoEscolar emitir(Long institucionId, Long alumnoId, Long planVersionId, String tipo, boolean incluyeAnexo) {
        this.modulos.exigir(institucionId, Modulo.DOCUMENTOS);
        String contenido = this.texto(institucionId, alumnoId, planVersionId, incluyeAnexo);
        byte[] pdf = this.pdf(contenido);
        String archivoId = this.archivos.guardar(institucionId, pdf);
        String folio = tipo + "-" + alumnoId + "-" + planVersionId;
        return (DocumentoEscolar)this.documentos.save(new DocumentoEscolar(institucionId, alumnoId, tipo, folio, archivoId));
    }

    private byte[] pdf(String contenido) {
        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            Document documento = new Document();
            PdfWriter.getInstance((Document)documento, (OutputStream)salida);
            documento.open();
            documento.add((Element)new Paragraph(contenido));
            documento.close();
            return salida.toByteArray();
        }
        catch (Exception e) {
            throw new NegocioException("No se pudo generar el documento de la escuela");
        }
    }

    @Generated
    public DocumentoService(DocumentoRepositorio documentos, AlumnoRepositorio alumnos, InstitucionRepositorio instituciones, CalificacionRepositorio calificaciones, DictamenRepositorio dictamenes, ArchivoEscolar archivos, ModuloGuardia modulos) {
        this.documentos = documentos;
        this.alumnos = alumnos;
        this.instituciones = instituciones;
        this.calificaciones = calificaciones;
        this.dictamenes = dictamenes;
        this.archivos = archivos;
        this.modulos = modulos;
    }
}
