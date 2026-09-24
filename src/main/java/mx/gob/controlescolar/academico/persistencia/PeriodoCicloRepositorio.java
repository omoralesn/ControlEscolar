package mx.gob.controlescolar.academico.persistencia;

import mx.gob.controlescolar.academico.dominio.PeriodoCiclo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PeriodoCicloRepositorio extends JpaRepository<PeriodoCiclo, Long> {
    List<PeriodoCiclo> findByCalendarioIdOrderByOrden(Long calendarioId);

    Optional<PeriodoCiclo> findByCalendarioIdAndOrden(Long calendarioId, int orden);
}
