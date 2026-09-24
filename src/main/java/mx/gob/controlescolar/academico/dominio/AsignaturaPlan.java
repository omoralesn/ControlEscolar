package mx.gob.controlescolar.academico.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Generated;
import mx.gob.controlescolar.academico.dominio.PlanVersion;

@Entity
@Table(name="asignaturas_planes")
public class AsignaturaPlan {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false)
    @JoinColumn(name="plan_version_id")
    private PlanVersion planVersion;
    private String clave;
    private String nombre;
    private Integer horas;
    private Integer creditos;
    private boolean optativa;
    private String modulo;
    private String submodulo;
    private boolean oficial;

    public AsignaturaPlan(PlanVersion planVersion, String clave, String nombre, Integer horas, Integer creditos, boolean optativa, String modulo, String submodulo, boolean oficial) {
        this.planVersion = planVersion;
        this.clave = clave;
        this.nombre = nombre;
        this.horas = horas;
        this.creditos = creditos;
        this.optativa = optativa;
        this.modulo = modulo;
        this.submodulo = submodulo;
        this.oficial = oficial;
    }

    public void reconocerOficial(String nombre, String modulo, String submodulo) {
        this.nombre = nombre;
        this.modulo = modulo;
        this.submodulo = submodulo;
        this.oficial = true;
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public PlanVersion getPlanVersion() {
        return this.planVersion;
    }

    @Generated
    public String getClave() {
        return this.clave;
    }

    @Generated
    public String getNombre() {
        return this.nombre;
    }

    @Generated
    public Integer getHoras() {
        return this.horas;
    }

    @Generated
    public Integer getCreditos() {
        return this.creditos;
    }

    @Generated
    public boolean isOptativa() {
        return this.optativa;
    }

    @Generated
    public String getModulo() {
        return this.modulo;
    }

    @Generated
    public String getSubmodulo() {
        return this.submodulo;
    }

    @Generated
    public boolean isOficial() {
        return this.oficial;
    }

    @Generated
    protected AsignaturaPlan() {
    }
}
