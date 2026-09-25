package mx.gob.controlescolar.acceso.dominio;

import mx.gob.controlescolar.comun.aplicacion.NegocioException;

import java.util.Map;

/**
 * Clave de centro de trabajo de 10 caracteres: entidad, clasificador, identificador,
 * consecutivo y dígito verificador (Anexo 4 de la SEP).
 */
public record ClaveCentroTrabajo(String texto, String entidad, String entidadNombre, char clasificador,
                                 String sostenimiento, String identificador, String nivel, String progresivo,
                                 char verificador) {

    private static final String TABLA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0";
    private static final Map<String, String> ENTIDADES = Map.ofEntries(
            Map.entry("01", "Aguascalientes"), Map.entry("02", "Baja California"),
            Map.entry("03", "Baja California Sur"), Map.entry("04", "Campeche"),
            Map.entry("05", "Coahuila"), Map.entry("06", "Colima"), Map.entry("07", "Chiapas"),
            Map.entry("08", "Chihuahua"), Map.entry("09", "Ciudad de México"), Map.entry("10", "Durango"),
            Map.entry("11", "Guanajuato"), Map.entry("12", "Guerrero"), Map.entry("13", "Hidalgo"),
            Map.entry("14", "Jalisco"), Map.entry("15", "México"), Map.entry("16", "Michoacán"),
            Map.entry("17", "Morelos"), Map.entry("18", "Nayarit"), Map.entry("19", "Nuevo León"),
            Map.entry("20", "Oaxaca"), Map.entry("21", "Puebla"), Map.entry("22", "Querétaro"),
            Map.entry("23", "Quintana Roo"), Map.entry("24", "San Luis Potosí"), Map.entry("25", "Sinaloa"),
            Map.entry("26", "Sonora"), Map.entry("27", "Tabasco"), Map.entry("28", "Tamaulipas"),
            Map.entry("29", "Tlaxcala"), Map.entry("30", "Veracruz"), Map.entry("31", "Yucatán"),
            Map.entry("32", "Zacatecas"));
    private static final Map<String, String> NIVELES = Map.ofEntries(
            Map.entry("JN", "PREESCOLAR"), Map.entry("CC", "PREESCOLAR"),
            Map.entry("PR", "PRIMARIA"), Map.entry("PB", "PRIMARIA"),
            Map.entry("ES", "SECUNDARIA"), Map.entry("TV", "SECUNDARIA"), Map.entry("ST", "SECUNDARIA"),
            Map.entry("BH", "MEDIA_SUPERIOR"), Map.entry("CB", "MEDIA_SUPERIOR"),
            Map.entry("TK", "MEDIA_SUPERIOR"), Map.entry("TC", "MEDIA_SUPERIOR"),
            Map.entry("CT", "MEDIA_SUPERIOR"), Map.entry("BT", "MEDIA_SUPERIOR"),
            Map.entry("UN", "SUPERIOR"), Map.entry("IT", "SUPERIOR"),
            Map.entry("UT", "SUPERIOR"), Map.entry("UP", "SUPERIOR"));

    public static ClaveCentroTrabajo analizar(String clave, String sostenimientoDeclarado) {
        if (clave == null) {
            throw new NegocioException("La CCT no tiene el formato de 10 caracteres");
        }
        clave = clave.trim().toUpperCase();
        if (!clave.matches("[0-9]{2}[A-Z][A-Z]{2}[0-9]{4}[A-Z0-9]")) {
            throw new NegocioException("La CCT no tiene el formato de 10 caracteres");
        }
        String cuerpo = clave.substring(0, 9);
        char esperado = verificadorDe(cuerpo);
        if (clave.charAt(9) != esperado) {
            throw new NegocioException("El dígito verificador de la CCT no coincide");
        }
        String entidad = clave.substring(0, 2);
        if (!ENTIDADES.containsKey(entidad)) {
            throw new NegocioException("La entidad de la CCT no existe");
        }
        char clasificador = clave.charAt(2);
        String sostenimiento = sostenimientoDe(clasificador, sostenimientoDeclarado);
        String identificador = clave.substring(3, 5);
        String nivel = NIVELES.get(identificador);
        if (nivel == null) {
            throw new NegocioException("El identificador de la CCT no corresponde a un nivel conocido");
        }
        return new ClaveCentroTrabajo(clave, entidad, ENTIDADES.get(entidad), clasificador, sostenimiento,
                identificador, nivel, clave.substring(5, 9), clave.charAt(9));
    }

    public static char verificadorDe(String cuerpo) {
        String digitos = expandir(cuerpo);
        if (digitos.length() != 12) {
            throw new NegocioException("No se pudo calcular el verificador de la CCT");
        }
        int izquierdos = 0;
        int derechos = 0;
        for (int par = 0; par < 6; par++) {
            izquierdos += digitos.charAt(par * 2) - '0';
            derechos += digitos.charAt(par * 2 + 1) - '0';
        }
        int resto = (izquierdos * 7 + derechos * 26) % 27;
        return TABLA.charAt(resto);
    }

    private static String expandir(String cuerpo) {
        StringBuilder digitos = new StringBuilder();
        for (int i = 0; i < cuerpo.length(); i++) {
            char caracter = cuerpo.charAt(i);
            if (caracter >= 'A' && caracter <= 'Z') {
                digitos.append(String.format("%02d", caracter - 'A' + 1));
            } else {
                digitos.append(caracter);
            }
        }
        return digitos.toString();
    }

    private static String sostenimientoDe(char clasificador, String declarado) {
        return switch (clasificador) {
            case 'E' -> coincide(declarado, "ESTATAL");
            case 'P' -> coincide(declarado, "PARTICULAR");
            case 'K' -> coincide(declarado, "CONAFE");
            case 'U' -> coincide(declarado, "AUTONOMO");
            case 'S' -> coincide(declarado, "SUBSIDIADO");
            case 'D' -> federalODesconcentrada(declarado);
            default -> throw new NegocioException("El clasificador de la CCT no es válido");
        };
    }

    private static String coincide(String declarado, String esperado) {
        if (declarado != null && !declarado.isBlank() && !esperado.equals(declarado)) {
            throw new NegocioException("El sostenimiento no corresponde al clasificador de la CCT");
        }
        return esperado;
    }

    private static String federalODesconcentrada(String declarado) {
        if (!"FEDERAL".equals(declarado) && !"DESCONCENTRADA".equals(declarado)) {
            throw new NegocioException("Una CCT con clasificador D debe declararse federal o desconcentrada");
        }
        return declarado;
    }
}
