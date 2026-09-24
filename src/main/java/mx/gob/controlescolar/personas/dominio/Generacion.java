package mx.gob.controlescolar.personas.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Generated;
import mx.gob.controlescolar.acceso.dominio.Institucion;

@Entity
@Table(name="generaciones")
public class Generacion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false)
    @JoinColumn(name="institucion_id")
    private Institucion institucion;
    private String nombre;
    private int anioInicio;

    public Generacion(Institucion institucion, String nombre, int anioInicio) {
        this.institucion = institucion;
        this.nombre = nombre;
        this.anioInicio = anioInicio;
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public Institucion getInstitucion() {
        return this.institucion;
    }

    @Generated
    public String getNombre() {
        return this.nombre;
    }

    @Generated
    public int getAnioInicio() {
        return this.anioInicio;
    }

    @Generated
    protected Generacion() {
    }
}
