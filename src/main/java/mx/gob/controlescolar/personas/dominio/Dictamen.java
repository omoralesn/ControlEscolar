package mx.gob.controlescolar.personas.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Generated;

@Entity
@Table(name="dictamenes")
public class Dictamen {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private Long institucionId;
    private Long alumnoId;
    private String tipo;
    private String dictamenPor;
    private String escuelaOrigen;
    private String cicloInicio;
    private String cicloFin;
    private String dependencia;
    private String estado;
    private String pais;
    private String expediente;
    private String folio;
    private BigDecimal promedio;
    private LocalDate fechaExpedicion;
    private String materias;

    public Dictamen(Long institucionId, Long alumnoId, String tipo, String dictamenPor, String escuelaOrigen, String cicloInicio, String cicloFin, String dependencia, String estado, String pais, String expediente, String folio, BigDecimal promedio, LocalDate fechaExpedicion, String materias) {
        this.institucionId = institucionId;
        this.alumnoId = alumnoId;
        this.tipo = tipo;
        this.dictamenPor = dictamenPor;
        this.escuelaOrigen = escuelaOrigen;
        this.cicloInicio = cicloInicio;
        this.cicloFin = cicloFin;
        this.dependencia = dependencia;
        this.estado = estado;
        this.pais = pais;
        this.expediente = expediente;
        this.folio = folio;
        this.promedio = promedio;
        this.fechaExpedicion = fechaExpedicion;
        this.materias = materias;
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
    public String getDictamenPor() {
        return this.dictamenPor;
    }

    @Generated
    public String getEscuelaOrigen() {
        return this.escuelaOrigen;
    }

    @Generated
    public String getCicloInicio() {
        return this.cicloInicio;
    }

    @Generated
    public String getCicloFin() {
        return this.cicloFin;
    }

    @Generated
    public String getDependencia() {
        return this.dependencia;
    }

    @Generated
    public String getEstado() {
        return this.estado;
    }

    @Generated
    public String getPais() {
        return this.pais;
    }

    @Generated
    public String getExpediente() {
        return this.expediente;
    }

    @Generated
    public String getFolio() {
        return this.folio;
    }

    @Generated
    public BigDecimal getPromedio() {
        return this.promedio;
    }

    @Generated
    public LocalDate getFechaExpedicion() {
        return this.fechaExpedicion;
    }

    @Generated
    public String getMaterias() {
        return this.materias;
    }

    @Generated
    protected Dictamen() {
    }
}
