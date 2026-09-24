package mx.gob.controlescolar.acceso.persistencia;

import mx.gob.controlescolar.acceso.dominio.Plantel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantelRepositorio
extends JpaRepository<Plantel, Long> {
}
