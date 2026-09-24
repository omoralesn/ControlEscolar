package mx.gob.controlescolar.acceso.persistencia;

import mx.gob.controlescolar.acceso.dominio.InstitucionModulo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitucionModuloRepositorio extends JpaRepository<InstitucionModulo, Long> {
    java.util.List<InstitucionModulo> findByInstitucionId(Long institucionId);

    boolean existsByInstitucionIdAndModulo(Long institucionId, mx.gob.controlescolar.acceso.dominio.Modulo modulo);

    void deleteByInstitucionId(Long institucionId);
}
