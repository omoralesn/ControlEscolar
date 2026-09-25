package mx.gob.controlescolar.acceso.dominio;

import java.util.List;

/**
 * Páginas que abre cada módulo de la plantilla. No se editan: habilitar el módulo habilita estas rutas.
 */
public final class CatalogoPaginas {

    public record Pagina(Modulo modulo, String ruta, String nombre) {
    }

    public static final List<Pagina> TODAS = List.of(
            new Pagina(Modulo.PLANES, "/planes", "Planes"),
            new Pagina(Modulo.PLANES, "/planes/programa", "Programa de la escuela"),
            new Pagina(Modulo.PLANES, "/calendarios", "Calendarios"),
            new Pagina(Modulo.PLANES, "/calendarios/propio", "Calendario propio"),
            new Pagina(Modulo.PLANTILLA, "/grupos", "Grupos y plantilla"),
            new Pagina(Modulo.PLANTILLA, "/grupos/profesores", "Profesores"),
            new Pagina(Modulo.PLANTILLA, "/grupos/horarios", "Horarios"),
            new Pagina(Modulo.PLANTILLA, "/escuela/datos", "Datos de la escuela"),
            new Pagina(Modulo.INSCRIPCION, "/grupos", "Inscripción"),
            new Pagina(Modulo.ALUMNOS, "/alumnos", "Alumnos"),
            new Pagina(Modulo.ALUMNOS, "/alumnos/movimientos", "Movimientos"),
            new Pagina(Modulo.ALUMNOS, "/estadistica/911", "Apoyo 911"),
            new Pagina(Modulo.EVALUACION, "/calificaciones", "Calificaciones"),
            new Pagina(Modulo.EVALUACION, "/asistencia", "Asistencia"),
            new Pagina(Modulo.DOCUMENTOS, "/documentos", "Documentos"),
            new Pagina(Modulo.DOCUMENTOS, "/documentos/kardex", "Kardex"),
            new Pagina(Modulo.DOCUMENTOS, "/documentos/boletas", "Boleta"),
            new Pagina(Modulo.DOCUMENTOS, "/documentos/constancias", "Constancia"),
            new Pagina(Modulo.PADRES, "/padres", "Padres"),
            new Pagina(Modulo.USUARIOS, "/usuarios", "Usuarios"),
            new Pagina(Modulo.USUARIOS, "/perfiles", "Perfiles"));

    private CatalogoPaginas() {
    }

    public static List<Pagina> de(Modulo modulo) {
        return TODAS.stream().filter(pagina -> pagina.modulo() == modulo).toList();
    }
}
