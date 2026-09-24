package mx.gob.controlescolar.academico.dominio;

public final class EsquemaNivel {

    private EsquemaNivel() {
    }

    public static boolean basica(String nivel) {
        return "PREESCOLAR".equals(nivel) || "PRIMARIA".equals(nivel) || "SECUNDARIA".equals(nivel);
    }
}
