package mx.gob.controlescolar.acceso.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Generated;
import mx.gob.controlescolar.acceso.dominio.ClaveCentroTrabajo;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Plantel;

@Entity
@Table(name="centros_trabajo")
public class CentroTrabajo {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false)
    @JoinColumn(name="institucion_id")
    private Institucion institucion;
    @ManyToOne(optional=false)
    @JoinColumn(name="plantel_id")
    private Plantel plantel;
    private String clave;
    private String nombre;
    private String entidadClave;
    private String entidadNombre;
    private String clasificador;
    private String sostenimiento;
    private String identificador;
    private String nivel;
    private String progresivo;
    private String verificador;

    public CentroTrabajo(Institucion institucion, Plantel plantel, String nombre, ClaveCentroTrabajo clave) {
        this.institucion = institucion;
        this.plantel = plantel;
        this.nombre = nombre;
        this.clave = clave.texto();
        this.entidadClave = clave.entidad();
        this.entidadNombre = clave.entidadNombre();
        this.clasificador = String.valueOf(clave.clasificador());
        this.sostenimiento = clave.sostenimiento();
        this.identificador = clave.identificador();
        this.nivel = clave.nivel();
        this.progresivo = clave.progresivo();
        this.verificador = String.valueOf(clave.verificador());
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
    public Plantel getPlantel() {
        return this.plantel;
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
    public String getEntidadClave() {
        return this.entidadClave;
    }

    @Generated
    public String getEntidadNombre() {
        return this.entidadNombre;
    }

    @Generated
    public String getClasificador() {
        return this.clasificador;
    }

    @Generated
    public String getSostenimiento() {
        return this.sostenimiento;
    }

    @Generated
    public String getIdentificador() {
        return this.identificador;
    }

    @Generated
    public String getNivel() {
        return this.nivel;
    }

    @Generated
    public String getProgresivo() {
        return this.progresivo;
    }

    @Generated
    public String getVerificador() {
        return this.verificador;
    }

    @Generated
    protected CentroTrabajo() {
    }
}
