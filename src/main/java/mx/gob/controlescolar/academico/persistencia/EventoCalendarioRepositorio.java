package mx.gob.controlescolar.academico.persistencia;

import mx.gob.controlescolar.academico.dominio.EventoCalendario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoCalendarioRepositorio extends JpaRepository<EventoCalendario, Long> {
    List<EventoCalendario> findByCalendarioId(Long calendarioId);
}
