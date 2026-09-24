package mx.gob.controlescolar.academico.dominio;

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
@Table(name = "periodos_planes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PeriodoPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_version_id")
    private PlanVersion planVersion;

    private int orden;
    private String nombre;

    public PeriodoPlan(PlanVersion planVersion, int orden, String nombre) {
        this.planVersion = planVersion;
        this.orden = orden;
        this.nombre = nombre;
    }
}
