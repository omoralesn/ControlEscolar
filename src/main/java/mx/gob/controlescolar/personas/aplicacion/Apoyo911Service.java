package mx.gob.controlescolar.personas.aplicacion;

import java.util.List;
import lombok.Generated;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.persistencia.InstitucionRepositorio;
import mx.gob.controlescolar.evaluacion.dominio.Calificacion;
import mx.gob.controlescolar.evaluacion.persistencia.CalificacionRepositorio;
import mx.gob.controlescolar.inscripcion.persistencia.ProfesorRepositorio;
import mx.gob.controlescolar.personas.dominio.Alumno;
import mx.gob.controlescolar.personas.persistencia.AlumnoDiscapacidadRepositorio;
import mx.gob.controlescolar.personas.persistencia.AlumnoRepositorio;
import mx.gob.controlescolar.personas.persistencia.InscripcionRepositorio;
import org.springframework.stereotype.Service;

@Service
public class Apoyo911Service {
    private final InstitucionRepositorio instituciones;
    private final AlumnoRepositorio alumnos;
    private final InscripcionRepositorio inscripciones;
    private final AlumnoDiscapacidadRepositorio discapacidades;
    private final CalificacionRepositorio calificaciones;
    private final ProfesorRepositorio profesores;
    private final ModuloGuardia modulos;

    public Resumen resumen(Long institucionId) {
        this.modulos.exigir(institucionId, Modulo.ALUMNOS);
        Institucion escuela = (Institucion)this.instituciones.findById(institucionId).orElseThrow();
        List<Alumno> plantel = this.alumnos.findByInstitucionId(institucionId);
        long activos = plantel.stream().filter(alumno -> "ACTIVO".equals(alumno.getEstatus())).count();
        long bajas = plantel.stream().filter(alumno -> "BAJA".equals(alumno.getEstatus())).count();
        long traslados = plantel.stream().filter(alumno -> "TRASLADO".equals(alumno.getEstatus())).count();
        long egresados = plantel.stream().filter(alumno -> "EGRESADO".equals(alumno.getEstatus())).count();
        long conDiscapacidad = plantel.stream().filter(alumno -> !this.discapacidades.findByAlumnoId(alumno.getId()).isEmpty()).count();
        long usanLentes = plantel.stream().filter(Alumno::isUsaLentes).count();
        long inscritosActivos = this.inscripciones.findByInstitucionIdAndHistoricaFalse(institucionId).size();
        long nuevoIngreso = plantel.stream().filter(alumno -> "ACTIVO".equals(alumno.getEstatus())).filter(alumno -> this.inscripciones.findByAlumnoId(alumno.getId()).size() == 1).count();
        long reprobados = this.calificaciones.findByInstitucionId(institucionId).stream().filter(calificacion -> calificacion.getValor() != null && calificacion.getValor().doubleValue() < 6.0).map(Calificacion::getAlumnoId).distinct().count();
        long regularizados = this.calificaciones.findByInstitucionId(institucionId).stream().filter(calificacion -> calificacion.getOrdenExtra() != null && Boolean.TRUE.equals(calificacion.getPresentado())).map(Calificacion::getAlumnoId).distinct().count();
        long plantilla = this.profesores.findByInstitucionId(institucionId).size();
        return new Resumen(escuela.getNombre(), escuela.getClaveCct(), plantel.size(), inscritosActivos, activos, bajas, traslados, egresados, conDiscapacidad, usanLentes, nuevoIngreso, reprobados, regularizados, plantilla);
    }

    @Generated
    public Apoyo911Service(InstitucionRepositorio instituciones, AlumnoRepositorio alumnos, InscripcionRepositorio inscripciones, AlumnoDiscapacidadRepositorio discapacidades, CalificacionRepositorio calificaciones, ProfesorRepositorio profesores, ModuloGuardia modulos) {
        this.instituciones = instituciones;
        this.alumnos = alumnos;
        this.inscripciones = inscripciones;
        this.discapacidades = discapacidades;
        this.calificaciones = calificaciones;
        this.profesores = profesores;
        this.modulos = modulos;
    }

    public record Resumen(String escuela, String cct, long alumnosRegistrados, long inscripcionesActivas, long activos, long bajas, long traslados, long egresados, long conDiscapacidad, long usanLentes, long nuevoIngreso, long reprobados, long regularizados, long plantilla) {
    }
}
