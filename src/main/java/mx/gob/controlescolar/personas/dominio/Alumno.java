package mx.gob.controlescolar.personas.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Generated;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.comun.dominio.Localidad;
import mx.gob.controlescolar.personas.dominio.Domicilio;
import mx.gob.controlescolar.personas.dominio.Generacion;

@Entity
@Table(name="alumnos")
public class Alumno {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false)
    @JoinColumn(name="institucion_id")
    private Institucion institucion;
    private String curp;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String estatus;
    private String sexo;
    private LocalDate fechaNacimiento;
    private boolean usaLentes;
    private boolean usaZapatoOrtopedico;
    @ManyToOne
    @JoinColumn(name="localidad_id")
    private Localidad localidad;
    @ManyToOne
    @JoinColumn(name="domicilio_id")
    private Domicilio domicilio;
    @ManyToOne
    @JoinColumn(name="generacion_id")
    private Generacion generacion;

    public Alumno(Institucion institucion, String curp, String nombre, String apellidoPaterno, String apellidoMaterno) {
        this.institucion = institucion;
        this.curp = curp;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.estatus = "ACTIVO";
    }

    public void actualizarIdentidad(String nombre, String apellidoPaterno, String apellidoMaterno, String sexo, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
    }

    public void asignarGeneracion(Generacion generacion) {
        this.generacion = generacion;
    }

    public void egresar() {
        this.estatus = "EGRESADO";
    }

    public void actualizarApoyos(boolean usaLentes, boolean usaZapatoOrtopedico) {
        this.usaLentes = usaLentes;
        this.usaZapatoOrtopedico = usaZapatoOrtopedico;
    }

    public String nombreCompleto() {
        return this.nombre + " " + this.apellidoPaterno + (String)(this.apellidoMaterno == null || this.apellidoMaterno.isBlank() ? "" : " " + this.apellidoMaterno);
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
    public String getCurp() {
        return this.curp;
    }

    @Generated
    public String getNombre() {
        return this.nombre;
    }

    @Generated
    public String getApellidoPaterno() {
        return this.apellidoPaterno;
    }

    @Generated
    public String getApellidoMaterno() {
        return this.apellidoMaterno;
    }

    @Generated
    public String getEstatus() {
        return this.estatus;
    }

    @Generated
    public String getSexo() {
        return this.sexo;
    }

    @Generated
    public LocalDate getFechaNacimiento() {
        return this.fechaNacimiento;
    }

    @Generated
    public boolean isUsaLentes() {
        return this.usaLentes;
    }

    @Generated
    public boolean isUsaZapatoOrtopedico() {
        return this.usaZapatoOrtopedico;
    }

    @Generated
    public Localidad getLocalidad() {
        return this.localidad;
    }

    @Generated
    public Domicilio getDomicilio() {
        return this.domicilio;
    }

    @Generated
    public Generacion getGeneracion() {
        return this.generacion;
    }

    @Generated
    protected Alumno() {
    }

    @Generated
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Generated
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    @Generated
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    @Generated
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    @Generated
    public void setUsaLentes(boolean usaLentes) {
        this.usaLentes = usaLentes;
    }

    @Generated
    public void setUsaZapatoOrtopedico(boolean usaZapatoOrtopedico) {
        this.usaZapatoOrtopedico = usaZapatoOrtopedico;
    }

    @Generated
    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    @Generated
    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }
}
