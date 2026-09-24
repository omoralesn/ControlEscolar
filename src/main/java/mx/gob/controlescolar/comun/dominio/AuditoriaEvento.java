package mx.gob.controlescolar.comun.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auditorias_eventos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long institucionId;
    private String accion;
    private String detalle;

    public AuditoriaEvento(Long institucionId, String accion, String detalle) {
        this.institucionId = institucionId;
        this.accion = accion;
        this.detalle = detalle;
    }
}
