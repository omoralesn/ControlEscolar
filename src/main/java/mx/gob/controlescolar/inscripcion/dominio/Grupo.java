package mx.gob.controlescolar.inscripcion.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Generated;
import mx.gob.controlescolar.academico.dominio.Programa;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.personas.dominio.Generacion;

@Entity
@Table(name="grupos")
public class Grupo {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false)
    @JoinColumn(name="institucion_id")
    private Institucion institucion;
    @ManyToOne(optional=false)
    @JoinColumn(name="programa_id")
    private Programa programa;
    private String nombre;
    private int periodoOrden;
    private String edificio;
    private String aula;
    private int capacidad;
    @ManyToOne
    @JoinColumn(name="generacion_id")
    private Generacion generacion;

    public Grupo(Institucion institucion, Programa programa, String nombre, int periodoOrden) {
        this.institucion = institucion;
        this.programa = programa;
        this.nombre = nombre;
        this.periodoOrden = periodoOrden;
    }

    public void ubicar(String edificio, String aula, int capacidad) {
        this.edificio = edificio;
        this.aula = aula;
        this.capacidad = capacidad;
    }

    public void asignarGeneracion(Generacion generacion) {
        this.generacion = generacion;
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
    public Programa getPrograma() {
        return this.programa;
    }

    @Generated
    public String getNombre() {
        return this.nombre;
    }

    @Generated
    public int getPeriodoOrden() {
        return this.periodoOrden;
    }

    public String getEdificio() {
        return this.edificio;
    }

    public String getAula() {
        return this.aula;
    }

    public int getCapacidad() {
        return this.capacidad;
    }

    @Generated
    public Generacion getGeneracion() {
        return this.generacion;
    }

    @Generated
    protected Grupo() {
    }
}
