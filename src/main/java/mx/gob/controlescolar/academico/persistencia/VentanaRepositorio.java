package mx.gob.controlescolar.academico.persistencia;

import java.util.List;
import mx.gob.controlescolar.academico.dominio.VentanaCaptura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentanaRepositorio
extends JpaRepository<VentanaCaptura, Long> {
    public List<VentanaCaptura> findByMomentoId(Long var1);

    public List<VentanaCaptura> findByPeriodoCicloId(Long var1);

    public void deleteByMomentoId(Long var1);
}
