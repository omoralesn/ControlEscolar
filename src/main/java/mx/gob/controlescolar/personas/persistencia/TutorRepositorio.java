package mx.gob.controlescolar.personas.persistencia;

import mx.gob.controlescolar.personas.dominio.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TutorRepositorio extends JpaRepository<Tutor, Long> {
    Optional<Tutor> findByUsuarioId(Long usuarioId);
}
