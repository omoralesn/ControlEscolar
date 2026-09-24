package mx.gob.controlescolar.comun.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "localidades")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Localidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "municipio_id")
    private Municipio municipio;

    private String nombre;

    public Localidad(Municipio municipio, String nombre) {
        this.municipio = municipio;
        this.nombre = nombre;
    }
}
