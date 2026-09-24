package mx.gob.controlescolar.padres.aplicacion;

import java.util.List;
import lombok.Generated;
import mx.gob.controlescolar.academico.aplicacion.CalendarioService;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.evaluacion.dominio.Calificacion;
import mx.gob.controlescolar.evaluacion.persistencia.CalificacionRepositorio;
import mx.gob.controlescolar.padres.aplicacion.AvisoService;
import mx.gob.controlescolar.padres.dominio.Aviso;
import mx.gob.controlescolar.personas.dominio.Tutor;
import mx.gob.controlescolar.personas.persistencia.AlumnoTutorRepositorio;
import mx.gob.controlescolar.personas.persistencia.TutorRepositorio;
import org.springframework.stereotype.Service;

@Service
public class PadreService {
    private final ModuloGuardia modulos;
    private final TutorRepositorio tutores;
    private final AlumnoTutorRepositorio vinculos;
    private final CalificacionRepositorio calificaciones;
    private final CalendarioService calendarios;
    private final AvisoService avisos;

    public Consulta consultar(Long institucionId, Long usuarioTutorId) {
        this.modulos.exigir(institucionId, Modulo.PADRES);
        Tutor tutor = this.tutores.findByUsuarioId(usuarioTutorId).orElseThrow(() -> new NegocioException("El tutor no existe"));
        List<Long> hijos = this.vinculos.findByTutorId(tutor.getId()).stream().map(v -> v.getAlumnoId()).toList();
        List<Calificacion> evaluaciones = hijos.isEmpty() ? List.of() : this.calificaciones.findByInstitucionIdAndAlumnoIdIn(institucionId, hijos).stream().filter(calificacion -> this.calendarios.visible(calificacion.getPlanVersionId(), calificacion.getPeriodoOrden(), calificacion.getMomentoId())).toList();
        List<Aviso> mensajes = this.avisos.deAlumnos(institucionId, hijos);
        return new Consulta(evaluaciones, mensajes);
    }

    @Generated
    public PadreService(ModuloGuardia modulos, TutorRepositorio tutores, AlumnoTutorRepositorio vinculos, CalificacionRepositorio calificaciones, CalendarioService calendarios, AvisoService avisos) {
        this.modulos = modulos;
        this.tutores = tutores;
        this.vinculos = vinculos;
        this.calificaciones = calificaciones;
        this.calendarios = calendarios;
        this.avisos = avisos;
    }

    public record Consulta(List<Calificacion> evaluaciones, List<Aviso> avisos) {
    }
}
