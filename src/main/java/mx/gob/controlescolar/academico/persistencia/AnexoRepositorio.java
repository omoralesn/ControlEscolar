package mx.gob.controlescolar.academico.persistencia;

import mx.gob.controlescolar.academico.dominio.AnexoVersion;
import mx.gob.controlescolar.academico.dominio.EstadoVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnexoRepositorio extends JpaRepository<AnexoVersion, Long> {
    Optional<AnexoVersion> findByProgramaIdAndEstado(Long programaId, EstadoVersion estado);
}
