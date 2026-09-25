package mx.gob.controlescolar.asistencia.persistencia;

import mx.gob.controlescolar.asistencia.dominio.ListaAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ListaRepositorio extends JpaRepository<ListaAsistencia, Long> {
    Optional<ListaAsistencia> findByInstitucionIdAndGrupoIdAndFecha(Long institucionId, Long grupoId, LocalDate fecha);
}
