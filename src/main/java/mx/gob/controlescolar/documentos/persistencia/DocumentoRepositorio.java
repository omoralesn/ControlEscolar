package mx.gob.controlescolar.documentos.persistencia;

import mx.gob.controlescolar.documentos.dominio.DocumentoEscolar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoRepositorio extends JpaRepository<DocumentoEscolar, Long> {
}
