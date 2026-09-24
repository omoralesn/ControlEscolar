package mx.gob.controlescolar.academico.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "momentos_evaluacion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MomentoEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_version_id")
    private PlanVersion planVersion;

    private int orden;
    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoMomento tipo;

    private boolean cuentaPromedio;

    public MomentoEvaluacion(PlanVersion planVersion, int orden, String nombre, TipoMomento tipo, boolean cuentaPromedio) {
        this.planVersion = planVersion;
        this.orden = orden;
        this.nombre = nombre;
        this.tipo = tipo;
        this.cuentaPromedio = cuentaPromedio;
    }
}
