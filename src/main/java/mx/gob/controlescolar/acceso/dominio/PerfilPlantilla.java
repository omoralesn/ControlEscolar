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
@Table(name = "perfiles_plantilla")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerfilPlantilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    public PerfilPlantilla(String nombre) {
        this.nombre = nombre;
    }
}
