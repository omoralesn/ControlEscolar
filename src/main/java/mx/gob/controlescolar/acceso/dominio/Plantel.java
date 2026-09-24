package mx.gob.controlescolar.acceso.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "planteles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plantel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private boolean activo = true;

    public Plantel(String nombre) {
        this.nombre = nombre;
        this.activo = true;
    }

    public void suspender() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }
}
