package mx.gob.controlescolar.acceso.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Generated;
import mx.gob.controlescolar.acceso.dominio.Plantel;

@Entity
@Table(name="instituciones")
public class Institucion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String claveCct;
    private boolean particular;
    private boolean activa = true;
    private String nivel;
    @ManyToOne
    @JoinColumn(name="plantel_id")
    private Plantel plantel;

    public Institucion(String nombre, String claveCct, boolean particular) {
        this.nombre = nombre;
        this.claveCct = claveCct;
        this.particular = particular;
    }

    public void suspender() {
        this.activa = false;
    }

    public void activar() {
        this.activa = true;
    }

    public void definirNivel(String nivel) {
        this.nivel = nivel;
    }

    public void asignarPlantel(Plantel plantel) {
        this.plantel = plantel;
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getNombre() {
        return this.nombre;
    }

    @Generated
    public String getClaveCct() {
        return this.claveCct;
    }

    @Generated
    public boolean isParticular() {
        return this.particular;
    }

    @Generated
    public boolean isActiva() {
        return this.activa;
    }

    @Generated
    public String getNivel() {
        return this.nivel;
    }

    @Generated
    public Plantel getPlantel() {
        return this.plantel;
    }

    @Generated
    protected Institucion() {
    }
}
