package mx.gob.controlescolar.comun.persistencia;

import mx.gob.controlescolar.comun.dominio.AuditoriaEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepositorio extends JpaRepository<AuditoriaEvento, Long> {
}
