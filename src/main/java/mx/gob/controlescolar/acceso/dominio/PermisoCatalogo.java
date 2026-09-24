package mx.gob.controlescolar.acceso.dominio;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;

public final class PermisoCatalogo {
    public static final List<Def> TODOS = List.of(new Def(Modulo.PLANTILLA, "PLANTILLA_CONSULTAR"), new Def(Modulo.PLANTILLA, "PLANTILLA_CAPTURAR"), new Def(Modulo.ALUMNOS, "ALUMNOS_CONSULTAR"), new Def(Modulo.ALUMNOS, "ALUMNOS_CAPTURAR"), new Def(Modulo.PLANES, "PLANES_CONSULTAR"), new Def(Modulo.PLANES, "PLANES_CONFIGURAR"), new Def(Modulo.PLANES, "PLANES_CERRAR"), new Def(Modulo.INSCRIPCION, "INSCRIPCION_CONSULTAR"), new Def(Modulo.INSCRIPCION, "INSCRIPCION_CAPTURAR"), new Def(Modulo.INSCRIPCION, "DICTAMEN_CONSULTAR"), new Def(Modulo.INSCRIPCION, "DICTAMEN_CAPTURAR"), new Def(Modulo.EVALUACION, "EVALUACION_CONSULTAR"), new Def(Modulo.EVALUACION, "EVALUACION_CAPTURAR"), new Def(Modulo.DOCUMENTOS, "DOCUMENTOS_CONSULTAR"), new Def(Modulo.DOCUMENTOS, "DOCUMENTOS_EMITIR"), new Def(Modulo.PADRES, "PADRES_CONSULTAR"));

    private PermisoCatalogo() {
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
