package mx.gob.controlescolar.personas.persistencia;

import mx.gob.controlescolar.personas.dominio.Domicilio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomicilioRepositorio extends JpaRepository<Domicilio, Long> {
}
