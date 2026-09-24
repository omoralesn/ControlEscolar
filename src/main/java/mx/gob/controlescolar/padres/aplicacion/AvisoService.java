package mx.gob.controlescolar.padres.aplicacion;

import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.padres.dominio.Aviso;
import mx.gob.controlescolar.padres.persistencia.AvisoRepositorio;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvisoService {

    private final AvisoRepositorio avisos;
    private final ModuloGuardia modulos;

    public void publicar(Long institucionId, Long alumnoId, String texto) {
        if (modulos.activo(institucionId, Modulo.PADRES)) {
            avisos.save(new Aviso(institucionId, alumnoId, texto));
        }
    }

    public java.util.List<Aviso> deAlumnos(Long institucionId, java.util.List<Long> alumnoIds) {
        if (alumnoIds.isEmpty()) {
            return java.util.List.of();
        }
        return avisos.findByInstitucionIdAndAlumnoIdIn(institucionId, alumnoIds);
    }
}
