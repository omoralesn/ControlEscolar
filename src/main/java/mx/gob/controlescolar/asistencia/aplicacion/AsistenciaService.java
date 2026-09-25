package mx.gob.controlescolar.asistencia.aplicacion;

import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.asistencia.dominio.AsistenciaAlumno;
import mx.gob.controlescolar.asistencia.dominio.FaltaProfesor;
import mx.gob.controlescolar.asistencia.dominio.ListaAsistencia;
import mx.gob.controlescolar.asistencia.persistencia.AsistenciaAlumnoRepositorio;
import mx.gob.controlescolar.asistencia.persistencia.FaltaProfesorRepositorio;
import mx.gob.controlescolar.asistencia.persistencia.ListaRepositorio;
import mx.gob.controlescolar.padres.aplicacion.AvisoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AsistenciaService {

    private final ListaRepositorio listas;
    private final AsistenciaAlumnoRepositorio asistencias;
    private final FaltaProfesorRepositorio faltasProfesor;
    private final ModuloGuardia modulos;
    private final AvisoService avisos;

    @Transactional
    public void registrarLista(Long institucionId, Long grupoId, LocalDate fecha, Long alumnoId, boolean presente) {
        modulos.exigir(institucionId, Modulo.EVALUACION);
        ListaAsistencia lista = listas.save(new ListaAsistencia(institucionId, grupoId, fecha));
        asistencias.save(new AsistenciaAlumno(lista.getId(), alumnoId, presente));
        if (!presente) {
            avisos.publicar(institucionId, alumnoId, "Se registró una falta");
        }
    }

    @Transactional
    public void registrarMatriz(Long institucionId, Long grupoId, LocalDate fecha, Map<Long, Boolean> presentes) {
        modulos.exigir(institucionId, Modulo.EVALUACION);
        ListaAsistencia lista = listas.findByInstitucionIdAndGrupoIdAndFecha(institucionId, grupoId, fecha)
                .orElseGet(() -> listas.save(new ListaAsistencia(institucionId, grupoId, fecha)));
        for (Map.Entry<Long, Boolean> celda : presentes.entrySet()) {
            boolean presente = Boolean.TRUE.equals(celda.getValue());
            var existente = asistencias.findByListaIdAndAlumnoId(lista.getId(), celda.getKey());
            boolean nueva = existente.isEmpty();
            AsistenciaAlumno marca = existente.orElseGet(() -> new AsistenciaAlumno(lista.getId(), celda.getKey(), presente));
            boolean antes = marca.isPresente();
            marca.setPresente(presente);
            asistencias.save(marca);
            if (!presente && (nueva || antes)) {
                avisos.publicar(institucionId, celda.getKey(), "Se registró una falta");
            }
        }
    }

    public Map<Long, Boolean> deLaLista(Long institucionId, Long grupoId, LocalDate fecha) {
        return listas.findByInstitucionIdAndGrupoIdAndFecha(institucionId, grupoId, fecha)
                .map(lista -> asistencias.findByListaId(lista.getId()).stream()
                        .collect(java.util.stream.Collectors.toMap(AsistenciaAlumno::getAlumnoId, AsistenciaAlumno::isPresente)))
                .orElseGet(Map::of);
    }

    @Transactional
    public void registrarFaltaProfesor(Long institucionId, Long profesorId, LocalDate fecha) {
        modulos.exigir(institucionId, Modulo.PLANTILLA);
        faltasProfesor.save(new FaltaProfesor(institucionId, profesorId, fecha));
    }

    public int faltasAlumno(Long alumnoId) {
        return asistencias.findByAlumnoIdAndPresenteFalse(alumnoId).size();
    }

    public int faltasProfesor(Long institucionId, Long profesorId) {
        return faltasProfesor.findByInstitucionIdAndProfesorId(institucionId, profesorId).size();
    }

    public List<AsistenciaAlumno> historicoAlumno(Long alumnoId) {
        return asistencias.findByAlumnoId(alumnoId);
    }

    public List<FaltaProfesor> historicoProfesor(Long institucionId, Long profesorId) {
        return faltasProfesor.findByInstitucionIdAndProfesorId(institucionId, profesorId);
    }
}
