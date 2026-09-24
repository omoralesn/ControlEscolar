package mx.gob.controlescolar.academico.persistencia;

import java.util.Optional;
import mx.gob.controlescolar.academico.dominio.CicloEscolar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CicloRepositorio
extends JpaRepository<CicloEscolar, Long> {
    public Optional<CicloEscolar> findByNombre(String var1);
}
