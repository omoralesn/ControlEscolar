package mx.gob.controlescolar.documentos.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "documentos_escolares")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentoEscolar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long institucionId;
    private Long alumnoId;
    private String tipo;
    private String folio;
    private LocalDate fechaEmision;
    private String archivoId;

    public DocumentoEscolar(Long institucionId, Long alumnoId, String tipo, String folio, String archivoId) {
        this.institucionId = institucionId;
        this.alumnoId = alumnoId;
        this.tipo = tipo;
        this.folio = folio;
        this.fechaEmision = LocalDate.now();
        this.archivoId = archivoId;
    }
}
