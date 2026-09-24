package mx.gob.controlescolar.padres.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "avisos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Aviso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long institucionId;
    private Long alumnoId;
    private String texto;

    public Aviso(Long institucionId, Long alumnoId, String texto) {
        this.institucionId = institucionId;
        this.alumnoId = alumnoId;
        this.texto = texto;
    }
}
