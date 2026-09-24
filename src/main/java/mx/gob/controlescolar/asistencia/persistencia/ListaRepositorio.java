package mx.gob.controlescolar.asistencia.persistencia;

import mx.gob.controlescolar.asistencia.dominio.ListaAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListaRepositorio extends JpaRepository<ListaAsistencia, Long> {
}
