package mx.gob.controlescolar.academico.persistencia;

import mx.gob.controlescolar.academico.dominio.AsignaturaAnexo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsignaturaAnexoRepositorio extends JpaRepository<AsignaturaAnexo, Long> {
    List<AsignaturaAnexo> findByAnexoVersionId(Long anexoVersionId);
}
