package mx.gob.controlescolar.academico.dominio;

import java.util.List;
import java.util.Optional;

public final class CatalogoPlanesSep {
    public static final PlanGeneral PREESCOLAR = new PlanGeneral("PREESCOLAR", "Preescolar", "BIMESTRAL", "Plan de Estudio para la educaci\u00f3n preescolar, primaria y secundaria (Acuerdo 14/08/22). En preescolar se observan los cuatro campos formativos.", List.of(CatalogoPlanesSep.materia("LEN", "Lenguajes", "Lenguajes", "Fase 2"), CatalogoPlanesSep.materia("SPC", "Saberes y pensamiento cient\u00edfico", "Saberes y pensamiento cient\u00edfico", "Fase 2"), CatalogoPlanesSep.materia("ENS", "\u00c9tica, naturaleza y sociedades", "\u00c9tica, naturaleza y sociedades", "Fase 2"), CatalogoPlanesSep.materia("DHC", "De lo humano y lo comunitario", "De lo humano y lo comunitario", "Fase 2")));
    public static final PlanGeneral PRIMARIA = new PlanGeneral("PRIMARIA", "Primaria", "BIMESTRAL", "Plan de Estudio 2022 (Acuerdo 14/08/22). Las disciplinas van dentro del campo formativo. La fase indica en qu\u00e9 grados se cursan.", List.of(CatalogoPlanesSep.materia("ESP", "Espa\u00f1ol", "Lenguajes", "Fases 3 a 5"), CatalogoPlanesSep.materia("ING", "Ingl\u00e9s", "Lenguajes", "Fases 3 a 5"), CatalogoPlanesSep.materia("ART", "Artes", "Lenguajes", "Fases 3 a 5"), CatalogoPlanesSep.materia("MAT", "Matem\u00e1ticas", "Saberes y pensamiento cient\u00edfico", "Fases 3 a 5"), CatalogoPlanesSep.materia("CDM", "Conocimiento del medio", "Saberes y pensamiento cient\u00edfico", "Fase 3, 1\u00b0 y 2\u00b0"), CatalogoPlanesSep.materia("CNA", "Ciencias naturales", "Saberes y pensamiento cient\u00edfico", "Fases 4 y 5, 3\u00b0 a 6\u00b0"), CatalogoPlanesSep.materia("FCE", "Formaci\u00f3n c\u00edvica y \u00e9tica", "\u00c9tica, naturaleza y sociedades", "Fases 3 a 5"), CatalogoPlanesSep.materia("HIS", "Historia", "\u00c9tica, naturaleza y sociedades", "Fases 4 y 5, 3\u00b0 a 6\u00b0"), CatalogoPlanesSep.materia("GEO", "Geograf\u00eda", "\u00c9tica, naturaleza y sociedades", "Fases 4 y 5, 3\u00b0 a 6\u00b0"), CatalogoPlanesSep.materia("EFI", "Educaci\u00f3n f\u00edsica", "De lo humano y lo comunitario", "Fases 3 a 5"), CatalogoPlanesSep.materia("ESO", "Educaci\u00f3n socioemocional", "De lo humano y lo comunitario", "Fases 3 a 5")));
    public static final PlanGeneral SECUNDARIA = new PlanGeneral("SECUNDARIA", "Secundaria", "BIMESTRAL", "Plan de Estudio 2022 (Acuerdo 14/08/22), fase 6. Cada disciplina pertenece a un campo formativo.", List.of(CatalogoPlanesSep.materia("ESP", "Espa\u00f1ol", "Lenguajes", "Fase 6"), CatalogoPlanesSep.materia("ING", "Ingl\u00e9s", "Lenguajes", "Fase 6"), CatalogoPlanesSep.materia("ART", "Artes", "Lenguajes", "Fase 6"), CatalogoPlanesSep.materia("MAT", "Matem\u00e1ticas", "Saberes y pensamiento cient\u00edfico", "Fase 6"), CatalogoPlanesSep.materia("BIO", "Biolog\u00eda", "Saberes y pensamiento cient\u00edfico", "Fase 6"), CatalogoPlanesSep.materia("FIS", "F\u00edsica", "Saberes y pensamiento cient\u00edfico", "Fase 6"), CatalogoPlanesSep.materia("QUI", "Qu\u00edmica", "Saberes y pensamiento cient\u00edfico", "Fase 6"), CatalogoPlanesSep.materia("HIS", "Historia", "\u00c9tica, naturaleza y sociedades", "Fase 6"), CatalogoPlanesSep.materia("GEO", "Geograf\u00eda", "\u00c9tica, naturaleza y sociedades", "Fase 6"), CatalogoPlanesSep.materia("FCE", "Formaci\u00f3n c\u00edvica y \u00e9tica", "\u00c9tica, naturaleza y sociedades", "Fase 6"), CatalogoPlanesSep.materia("TEC", "Tecnolog\u00eda", "De lo humano y lo comunitario", "Fase 6"), CatalogoPlanesSep.materia("EFI", "Educaci\u00f3n f\u00edsica", "De lo humano y lo comunitario", "Fase 6"), CatalogoPlanesSep.materia("ESO", "Educaci\u00f3n socioemocional", "De lo humano y lo comunitario", "Fase 6")));
    public static final PlanGeneral MEDIA_SUPERIOR = new PlanGeneral("MEDIA_SUPERIOR", "Bachillerato general", "SEMESTRAL", "Marco Curricular Com\u00fan de la Educaci\u00f3n Media Superior (Acuerdo 09/08/23). El componente de formaci\u00f3n fundamental es el mismo para el bachillerato general.", List.of(CatalogoPlanesSep.materia("LCO", "Lengua y comunicaci\u00f3n", "Recurso sociocognitivo", "Curr\u00edculo fundamental"), CatalogoPlanesSep.materia("PMA", "Pensamiento matem\u00e1tico", "Recurso sociocognitivo", "Curr\u00edculo fundamental"), CatalogoPlanesSep.materia("CHI", "Conciencia hist\u00f3rica", "Recurso sociocognitivo", "Curr\u00edculo fundamental"), CatalogoPlanesSep.materia("CDI", "Cultura digital", "Recurso sociocognitivo", "Curr\u00edculo fundamental"), CatalogoPlanesSep.materia("CNE", "Ciencias naturales, experimentales y tecnolog\u00eda", "\u00c1rea de conocimiento", "Curr\u00edculo fundamental"), CatalogoPlanesSep.materia("CSO", "Ciencias sociales", "\u00c1rea de conocimiento", "Curr\u00edculo fundamental"), CatalogoPlanesSep.materia("HUM", "Humanidades", "\u00c1rea de conocimiento", "Curr\u00edculo fundamental")));
    public static final PlanGeneral SUPERIOR = new PlanGeneral("SUPERIOR", "Licenciatura de ejemplo", "CUATRIMESTRAL", "La SEP no publica una lista nacional de asignaturas para toda la educaci\u00f3n superior. Las materias son las del plan autorizado del programa.", List.of());
    public static final List<PlanGeneral> GENERALES = List.of(PREESCOLAR, PRIMARIA, SECUNDARIA, MEDIA_SUPERIOR, SUPERIOR);

    private CatalogoPlanesSep() {
    }

    public static Optional<PlanGeneral> de(String nivel, String nombre) {
        return GENERALES.stream().filter(plan -> plan.nivel().equals(nivel) && plan.nombre().equals(nombre)).findFirst();
    }

    public static boolean tieneListaNacional(String nivel, String nombre) {
        return CatalogoPlanesSep.de(nivel, nombre).map(plan -> !plan.asignaturas().isEmpty()).orElse(false);
    }

    private static AsignaturaOficial materia(String clave, String nombre, String campo, String fase) {
        return new AsignaturaOficial(clave, nombre, campo, fase);
    }

    public record AsignaturaOficial(String clave, String nombre, String campo, String fase) {
    }

    public record PlanGeneral(String nivel, String nombre, String tipoPeriodo, String fundamento, List<AsignaturaOficial> asignaturas) {
    }
}
