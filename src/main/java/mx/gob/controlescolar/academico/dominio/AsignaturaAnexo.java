package mx.gob.controlescolar.academico.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asignaturas_anexos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AsignaturaAnexo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "anexo_version_id")
    private AnexoVersion anexoVersion;

    private String clave;
    private String nombre;

    public AsignaturaAnexo(AnexoVersion anexoVersion, String clave, String nombre) {
        this.anexoVersion = anexoVersion;
        this.clave = clave;
        this.nombre = nombre;
    }
}
