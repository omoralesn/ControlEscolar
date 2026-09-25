package mx.gob.controlescolar.acceso.dominio;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;

public final class PermisoCatalogo {
    public static final List<Def> TODOS = List.of(
            new Def(Modulo.PLANTILLA, "PLANTILLA_CONSULTAR"),
            new Def(Modulo.PLANTILLA, "PLANTILLA_CAPTURAR"),
            new Def(Modulo.PLANTILLA, "ESCUELA_DATOS"),
            new Def(Modulo.ALUMNOS, "ALUMNOS_CONSULTAR"),
            new Def(Modulo.ALUMNOS, "ALUMNOS_CAPTURAR"),
            new Def(Modulo.PLANES, "PLANES_CONSULTAR"),
            new Def(Modulo.PLANES, "PLANES_CONFIGURAR"),
            new Def(Modulo.PLANES, "PLANES_CERRAR"),
            new Def(Modulo.INSCRIPCION, "INSCRIPCION_CONSULTAR"),
            new Def(Modulo.INSCRIPCION, "INSCRIPCION_CAPTURAR"),
            new Def(Modulo.INSCRIPCION, "DICTAMEN_CONSULTAR"),
            new Def(Modulo.INSCRIPCION, "DICTAMEN_CAPTURAR"),
            new Def(Modulo.EVALUACION, "EVALUACION_CONSULTAR"),
            new Def(Modulo.EVALUACION, "EVALUACION_CAPTURAR"),
            new Def(Modulo.DOCUMENTOS, "DOCUMENTOS_CONSULTAR"),
            new Def(Modulo.DOCUMENTOS, "DOCUMENTOS_EMITIR"),
            new Def(Modulo.PADRES, "PADRES_CONSULTAR"),
            new Def(Modulo.USUARIOS, "USUARIOS_CONSULTAR"),
            new Def(Modulo.USUARIOS, "USUARIOS_CAPTURAR"));

    private static final Map<String, String> ETIQUETAS = Map.ofEntries(
            Map.entry("PLANTILLA_CONSULTAR", "Consultar plantilla"),
            Map.entry("PLANTILLA_CAPTURAR", "Capturar plantilla"),
            Map.entry("ESCUELA_DATOS", "Datos de la escuela"),
            Map.entry("ALUMNOS_CONSULTAR", "Consultar alumnos"),
            Map.entry("ALUMNOS_CAPTURAR", "Capturar alumnos"),
            Map.entry("PLANES_CONSULTAR", "Consultar planes"),
            Map.entry("PLANES_CONFIGURAR", "Configurar planes"),
            Map.entry("PLANES_CERRAR", "Cerrar planes"),
            Map.entry("INSCRIPCION_CONSULTAR", "Consultar inscripción"),
            Map.entry("INSCRIPCION_CAPTURAR", "Capturar inscripción"),
            Map.entry("DICTAMEN_CONSULTAR", "Consultar dictamen"),
            Map.entry("DICTAMEN_CAPTURAR", "Capturar dictamen"),
            Map.entry("EVALUACION_CONSULTAR", "Consultar evaluación"),
            Map.entry("EVALUACION_CAPTURAR", "Capturar evaluación"),
            Map.entry("DOCUMENTOS_CONSULTAR", "Consultar documentos"),
            Map.entry("DOCUMENTOS_EMITIR", "Emitir documentos"),
            Map.entry("PADRES_CONSULTAR", "Consultar padres"),
            Map.entry("USUARIOS_CONSULTAR", "Consultar usuarios"),
            Map.entry("USUARIOS_CAPTURAR", "Capturar usuarios"));

    private PermisoCatalogo() {
    }

    public static String etiqueta(String codigo) {
        return ETIQUETAS.getOrDefault(codigo, codigo);
    }

    public static Modulo moduloDe(String codigo) {
        return TODOS.stream().filter(def -> def.codigo().equals(codigo)).map(Def::modulo).findFirst().orElseThrow(() -> new NegocioException("El permiso no existe"));
    }

    public static Set<String> de(Set<Modulo> modulos) {
        return TODOS.stream().filter(def -> modulos.contains((Object)def.modulo())).map(Def::codigo).collect(Collectors.toSet());
    }

    public record Def(Modulo modulo, String codigo) {
    }
}
