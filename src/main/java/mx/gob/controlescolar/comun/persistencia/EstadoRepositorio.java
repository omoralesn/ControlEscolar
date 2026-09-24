package mx.gob.controlescolar.comun.persistencia;

import mx.gob.controlescolar.comun.dominio.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoRepositorio extends JpaRepository<Estado, Long> {
}
