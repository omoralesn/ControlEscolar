package mx.gob.controlescolar.evaluacion.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Generated;

@Entity
@Table(name="calificaciones")
public class Calificacion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private Long institucionId;
    private Long alumnoId;
    private Long planVersionId;
    private String asignaturaClave;
    private int periodoOrden;
    private Long momentoId;
    private BigDecimal valor;
    private String observacion;
    private Integer faltas;
    private String acta;
    private boolean complementaria;
    private String causa;
    private Boolean presentado;
    private Integer ordenExtra;

    public Calificacion(Long institucionId, Long alumnoId, Long planVersionId, String asignaturaClave, int periodoOrden, Long momentoId, BigDecimal valor, String observacion, boolean complementaria) {
        this.institucionId = institucionId;
        this.alumnoId = alumnoId;
        this.planVersionId = planVersionId;
        this.asignaturaClave = asignaturaClave;
        this.periodoOrden = periodoOrden;
        this.momentoId = momentoId;
        this.valor = valor;
        this.observacion = observacion;
        this.complementaria = complementaria;
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
    public Long getPlanVersionId() {
        return this.planVersionId;
    }

    @Generated
    public String getAsignaturaClave() {
        return this.asignaturaClave;
    }

    @Generated
    public int getPeriodoOrden() {
        return this.periodoOrden;
    }

    @Generated
    public Long getMomentoId() {
        return this.momentoId;
    }

    @Generated
    public BigDecimal getValor() {
        return this.valor;
    }

    @Generated
    public String getObservacion() {
        return this.observacion;
    }

    @Generated
    public Integer getFaltas() {
        return this.faltas;
    }

    @Generated
    public String getActa() {
        return this.acta;
    }

    @Generated
    public boolean isComplementaria() {
        return this.complementaria;
    }

    @Generated
    public String getCausa() {
        return this.causa;
    }

    @Generated
    public Boolean getPresentado() {
        return this.presentado;
    }

    @Generated
    public Integer getOrdenExtra() {
        return this.ordenExtra;
    }

    @Generated
    protected Calificacion() {
    }

    @Generated
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Generated
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    @Generated
    public void setFaltas(Integer faltas) {
        this.faltas = faltas;
    }

    @Generated
    public void setActa(String acta) {
        this.acta = acta;
    }

    @Generated
    public void setCausa(String causa) {
        this.causa = causa;
    }

    @Generated
    public void setPresentado(Boolean presentado) {
        this.presentado = presentado;
    }

    @Generated
    public void setOrdenExtra(Integer ordenExtra) {
        this.ordenExtra = ordenExtra;
    }
}
