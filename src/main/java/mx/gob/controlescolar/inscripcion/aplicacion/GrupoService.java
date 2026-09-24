package mx.gob.controlescolar.inscripcion.aplicacion;

import java.util.List;
import lombok.Generated;
import mx.gob.controlescolar.academico.dominio.Programa;
import mx.gob.controlescolar.academico.persistencia.ProgramaRepositorio;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.persistencia.InstitucionRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.inscripcion.dominio.AsignacionDocente;
import mx.gob.controlescolar.inscripcion.dominio.Grupo;
import mx.gob.controlescolar.inscripcion.dominio.Horario;
import mx.gob.controlescolar.inscripcion.dominio.Profesor;
import mx.gob.controlescolar.inscripcion.persistencia.AsignacionDocenteRepositorio;
import mx.gob.controlescolar.inscripcion.persistencia.GrupoRepositorio;
import mx.gob.controlescolar.inscripcion.persistencia.HorarioRepositorio;
import mx.gob.controlescolar.inscripcion.persistencia.ProfesorRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GrupoService {
    private final GrupoRepositorio grupos;
    private final ProfesorRepositorio profesores;
    private final HorarioRepositorio horarios;
    private final AsignacionDocenteRepositorio asignaciones;
    private final ProgramaRepositorio programas;
    private final InstitucionRepositorio instituciones;
    private final ModuloGuardia modulos;

    @Transactional
    public Profesor registrarProfesor(Long institucionId, String nombre) {
        this.modulos.exigir(institucionId, Modulo.PLANTILLA);
        Institucion institucion = (Institucion)this.instituciones.findById(institucionId).orElseThrow();
        return (Profesor)this.profesores.save(new Profesor(institucion, nombre));
    }

    @Transactional
    public Grupo registrar(Long institucionId, Long programaId, String nombre, int periodoOrden) {
        this.modulos.exigir(institucionId, Modulo.INSCRIPCION);
        Programa programa = (Programa)this.programas.findById(programaId).orElseThrow();
        if (!programa.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El programa no pertenece a la escuela");
        }
        return (Grupo)this.grupos.save(new Grupo(programa.getInstitucion(), programa, nombre, periodoOrden));
    }

    public List<Grupo> consultar(Long institucionId) {
        return this.grupos.findByInstitucionId(institucionId);
    }

    @Transactional
    public Horario asignar(Long institucionId, Long grupoId, Long profesorId, String asignatura, String dia,
                           String horaInicio, String horaFin) {
        this.modulos.exigir(institucionId, Modulo.PLANTILLA);
        Grupo grupo = this.grupos.findById(grupoId).orElseThrow();
        Profesor profesor = this.profesores.findById(profesorId).orElseThrow();
        if (!grupo.getInstitucion().getId().equals(institucionId) || !profesor.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El grupo o el profesor no pertenecen a la escuela");
        }
        for (Horario existente : this.horarios.findByProfesorIdAndDia(profesorId, dia)) {
            if (this.seEmpalman(existente.getHoraInicio(), existente.getHoraFin(), horaInicio, horaFin)) {
                throw new NegocioException("El profesor ya tiene horario en ese día");
            }
        }
        AsignacionDocente asignacion = this.asignaciones
                .findByGrupoIdAndProfesorIdAndAsignaturaClave(grupoId, profesorId, asignatura)
                .orElseGet(() -> this.asignaciones.save(new AsignacionDocente(institucionId, grupo, profesor, asignatura)));
        Horario horario = new Horario(institucionId, grupo, profesor, asignatura, dia, horaInicio, horaFin);
        horario.ligar(asignacion);
        return this.horarios.save(horario);
    }

    public List<Profesor> plantilla(Long institucionId) {
        this.modulos.exigir(institucionId, Modulo.PLANTILLA);
        return this.profesores.findByInstitucionId(institucionId);
    }

    public List<Horario> horarios(Long institucionId) {
        this.modulos.exigir(institucionId, Modulo.PLANTILLA);
        return this.horarios.findByInstitucionId(institucionId);
    }

    private boolean seEmpalman(String inicioA, String finA, String inicioB, String finB) {
        return inicioA.compareTo(finB) < 0 && inicioB.compareTo(finA) < 0;
    }

    @Generated
    public GrupoService(GrupoRepositorio grupos, ProfesorRepositorio profesores, HorarioRepositorio horarios, AsignacionDocenteRepositorio asignaciones, ProgramaRepositorio programas, InstitucionRepositorio instituciones, ModuloGuardia modulos) {
        this.grupos = grupos;
        this.profesores = profesores;
        this.horarios = horarios;
        this.asignaciones = asignaciones;
        this.programas = programas;
        this.instituciones = instituciones;
        this.modulos = modulos;
    }
}
