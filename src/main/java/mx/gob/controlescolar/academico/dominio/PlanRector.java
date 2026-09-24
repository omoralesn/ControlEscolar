package mx.gob.controlescolar.academico.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "planes_rectores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanRector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nivel;
    private String nombre;
    private String tipoPeriodo;

    public PlanRector(String nivel, String nombre, String tipoPeriodo) {
        this.nivel = nivel;
        this.nombre = nombre;
        this.tipoPeriodo = tipoPeriodo;
    }
}
