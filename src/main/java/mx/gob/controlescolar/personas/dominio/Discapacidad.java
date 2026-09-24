package mx.gob.controlescolar.personas.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Generated;

@Entity
@Table(name="discapacidades")
public class Discapacidad {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String codigo;
    private String nombre;

    public Discapacidad(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getCodigo() {
        return this.codigo;
    }

    @Generated
    public String getNombre() {
        return this.nombre;
    }

    @Generated
    protected Discapacidad() {
    }
}
