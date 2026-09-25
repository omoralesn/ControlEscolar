package mx.gob.controlescolar.comun.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Bitácora de acceso (alta escuela/usuario/perfil, vigencia, módulos, datos).
 * Las capturas académicas (p. ej. calificaciones) se auditarán en una entrega posterior.
 */
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
    private Long actorId;
    private String actorLogin;
    private String afectadoLogin;
    private String perfilNombre;
    private Instant ocurridoEn;

    public AuditoriaEvento(Long institucionId, String accion, String detalle, Long actorId, String actorLogin,
                           String afectadoLogin, String perfilNombre) {
        this.institucionId = institucionId;
        this.accion = accion;
        this.detalle = detalle;
        this.actorId = actorId;
        this.actorLogin = actorLogin;
        this.afectadoLogin = afectadoLogin;
        this.perfilNombre = perfilNombre;
        this.ocurridoEn = Instant.now();
    }

    /** Compatibilidad con registros previos sin actor. */
    public AuditoriaEvento(Long institucionId, String accion, String detalle) {
        this(institucionId, accion, detalle, null, null, null, null);
    }
}
