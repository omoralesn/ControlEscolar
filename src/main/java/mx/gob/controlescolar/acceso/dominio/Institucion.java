package mx.gob.controlescolar.acceso.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mx.gob.controlescolar.personas.dominio.Domicilio;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "instituciones")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Institucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String claveCct;
    private boolean particular;
    private boolean activa = true;
    private String nivel;

    @ManyToOne
    @JoinColumn(name = "plantel_id")
    private Plantel plantel;

    @ManyToOne
    @JoinColumn(name = "domicilio_id")
    private Domicilio domicilio;

    private String telefono;
    private String celular;
    private String correo;
    private String contactoNombre;

    public Institucion(String nombre, String claveCct, boolean particular) {
        this.nombre = nombre;
        this.claveCct = claveCct;
        this.particular = particular;
        this.activa = true;
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

    public void asignarDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    public void definirContacto(String contactoNombre, String telefono, String celular, String correo) {
        this.contactoNombre = contactoNombre;
        this.telefono = telefono;
        this.celular = celular;
        this.correo = correo;
    }
}
