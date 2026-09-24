package mx.gob.controlescolar.personas.persistencia;

import mx.gob.controlescolar.personas.dominio.Discapacidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscapacidadRepositorio extends JpaRepository<Discapacidad, Long> {
    Optional<Discapacidad> findByCodigo(String codigo);
}
