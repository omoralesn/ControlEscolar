package mx.gob.controlescolar.personas.aplicacion;

import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.personas.dominio.Dictamen;
import mx.gob.controlescolar.personas.persistencia.AlumnoRepositorio;
import mx.gob.controlescolar.personas.persistencia.DictamenRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DictamenService {

    private final DictamenRepositorio dictamenes;
    private final AlumnoRepositorio alumnos;
    private final ModuloGuardia modulos;

    @Transactional
    public Dictamen revalidar(Long institucionId, Long alumnoId, String dictamenPor, String escuelaOrigen,
                              String cicloInicio, String cicloFin, String estado, String pais, String expediente,
                              String folio, LocalDate fecha, String materias) {
        exigir(institucionId, alumnoId);
        if (pais == null || pais.isBlank()) {
            throw new NegocioException("La revalidación exige el país de la escuela de origen");
        }
        return guardar(institucionId, alumnoId, "REVALIDACION", dictamenPor, escuelaOrigen, cicloInicio, cicloFin,
                null, estado, pais, expediente, folio, null, fecha, materias);
    }

    @Transactional
    public Dictamen equivaler(Long institucionId, Long alumnoId, String dictamenPor, String escuelaOrigen,
                              String cicloInicio, String cicloFin, String dependencia, String estado,
                              String expediente, BigDecimal promedio, LocalDate fecha, String materias) {
        exigir(institucionId, alumnoId);
        if (promedio == null || promedio.compareTo(new BigDecimal("6.0")) < 0
                || promedio.compareTo(new BigDecimal("10.0")) > 0) {
            throw new NegocioException("La equivalencia exige un promedio entre 6.0 y 10.0");
        }
        if (estado == null || estado.isBlank()) {
            throw new NegocioException("La equivalencia exige el estado de la escuela de origen");
        }
        return guardar(institucionId, alumnoId, "EQUIVALENCIA", dictamenPor, escuelaOrigen, cicloInicio, cicloFin,
                dependencia, estado, null, expediente, null, promedio, fecha, materias);
    }

    public List<Dictamen> delAlumno(Long alumnoId) {
        return dictamenes.findByAlumnoId(alumnoId);
    }

    private Dictamen guardar(Long institucionId, Long alumnoId, String tipo, String dictamenPor, String escuelaOrigen,
                             String cicloInicio, String cicloFin, String dependencia, String estado, String pais,
                             String expediente, String folio, BigDecimal promedio, LocalDate fecha, String materias) {
        if (escuelaOrigen == null || escuelaOrigen.isBlank() || dictamenPor == null || dictamenPor.isBlank()) {
            throw new NegocioException("El dictamen exige escuela de origen y semestre o grado");
        }
        return dictamenes.save(new Dictamen(institucionId, alumnoId, tipo, dictamenPor, escuelaOrigen, cicloInicio,
                cicloFin, dependencia, estado, pais, expediente, folio, promedio, fecha, materias));
    }

    private void exigir(Long institucionId, Long alumnoId) {
        modulos.exigir(institucionId, Modulo.INSCRIPCION);
        var alumno = alumnos.findById(alumnoId).orElseThrow(() -> new NegocioException("El alumno no existe"));
        if (!alumno.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El alumno no pertenece a la escuela");
        }
    }
}
