package mx.gob.controlescolar.inscripcion.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mx.gob.controlescolar.acceso.dominio.Institucion;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profesores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profesor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "institucion_id")
    private Institucion institucion;

    private String nombre;

    public Profesor(Institucion institucion, String nombre) {
        this.institucion = institucion;
        this.nombre = nombre;
    }
}
