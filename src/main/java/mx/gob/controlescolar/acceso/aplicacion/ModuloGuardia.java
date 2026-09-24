package mx.gob.controlescolar.acceso.aplicacion;

import mx.gob.controlescolar.acceso.dominio.InstitucionModulo;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.persistencia.InstitucionModuloRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuloGuardia {

    private final InstitucionModuloRepositorio modulos;

    public void exigir(Long institucionId, Modulo modulo) {
        if (institucionId == null || !modulos.existsByInstitucionIdAndModulo(institucionId, modulo)) {
            throw new NegocioException("La escuela no tiene activo el módulo " + modulo);
        }
    }

    public boolean activo(Long institucionId, Modulo modulo) {
        return institucionId != null && modulos.existsByInstitucionIdAndModulo(institucionId, modulo);
    }

    public Set<Modulo> activos(Long institucionId) {
        return modulos.findByInstitucionId(institucionId).stream()
                .map(InstitucionModulo::getModulo)
                .collect(Collectors.toSet());
    }
}
