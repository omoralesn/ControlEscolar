package mx.gob.controlescolar.personas.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Generated;

@Entity
@Table(name="movimientos_alumnos")
public class MovimientoAlumno {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private Long institucionId;
    private Long alumnoId;
    private String tipo;
    private String detalle;
    private LocalDate fecha;

    public MovimientoAlumno(Long institucionId, Long alumnoId, String tipo, String detalle) {
        this.institucionId = institucionId;
        this.alumnoId = alumnoId;
        this.tipo = tipo;
        this.detalle = detalle;
        this.fecha = LocalDate.now();
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public Long getInstitucionId() {
        return this.institucionId;
    }

    @Generated
    public Long getAlumnoId() {
        return this.alumnoId;
    }

    @Generated
    public String getTipo() {
        return this.tipo;
    }

    @Generated
    public String getDetalle() {
        return this.detalle;
    }

    @Generated
    public LocalDate getFecha() {
        return this.fecha;
    }

    @Generated
    protected MovimientoAlumno() {
    }
}
