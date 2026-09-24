package mx.gob.controlescolar.personas.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Generated;
import mx.gob.controlescolar.personas.dominio.Domicilio;

@Entity
@Table(name="tutores")
public class Tutor {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private Long institucionId;
    private String nombre;
    private Long usuarioId;
    private String curp;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String parentesco;
    private String telefono;
    private String celular;
    private String correo;
    private String estudios;
    private String ocupacion;
    private boolean viveConAlumno = true;
    @ManyToOne
    @JoinColumn(name="domicilio_id")
    private Domicilio domicilio;

    public Tutor(Long institucionId, String nombre, Long usuarioId) {
        this.institucionId = institucionId;
        this.nombre = nombre;
        this.usuarioId = usuarioId;
    }

    public void actualizar(String curp, String nombre, String apellidoPaterno, String apellidoMaterno, String parentesco, String telefono, String celular, String correo, String estudios, String ocupacion, boolean viveConAlumno) {
        this.curp = curp;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.parentesco = parentesco;
        this.telefono = telefono;
        this.celular = celular;
        this.correo = correo;
        this.estudios = estudios;
        this.ocupacion = ocupacion;
        this.viveConAlumno = viveConAlumno;
    }

    public String nombreCompleto() {
        StringBuilder sb = new StringBuilder(this.nombre == null ? "" : this.nombre);
        if (this.apellidoPaterno != null && !this.apellidoPaterno.isBlank()) {
            sb.append(' ').append(this.apellidoPaterno);
        }
        if (this.apellidoMaterno != null && !this.apellidoMaterno.isBlank()) {
            sb.append(' ').append(this.apellidoMaterno);
        }
        return sb.toString().trim();
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
    public String getNombre() {
        return this.nombre;
    }

    @Generated
    public Long getUsuarioId() {
        return this.usuarioId;
    }

    @Generated
    public String getCurp() {
        return this.curp;
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
    public String getParentesco() {
        return this.parentesco;
    }

    @Generated
    public String getTelefono() {
        return this.telefono;
    }

    @Generated
    public String getCelular() {
        return this.celular;
    }

    @Generated
    public String getCorreo() {
        return this.correo;
    }

    @Generated
    public String getEstudios() {
        return this.estudios;
    }

    @Generated
    public String getOcupacion() {
        return this.ocupacion;
    }

    @Generated
    public boolean isViveConAlumno() {
        return this.viveConAlumno;
    }

    @Generated
    public Domicilio getDomicilio() {
        return this.domicilio;
    }

    @Generated
    protected Tutor() {
    }

    @Generated
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Generated
    public void setCurp(String curp) {
        this.curp = curp;
    }

    @Generated
    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    @Generated
    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    @Generated
    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    @Generated
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Generated
    public void setCelular(String celular) {
        this.celular = celular;
    }

    @Generated
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Generated
    public void setEstudios(String estudios) {
        this.estudios = estudios;
    }

    @Generated
    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    @Generated
    public void setViveConAlumno(boolean viveConAlumno) {
        this.viveConAlumno = viveConAlumno;
    }

    @Generated
    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }
}
