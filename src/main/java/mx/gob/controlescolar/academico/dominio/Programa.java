package mx.gob.controlescolar.academico.dominio;

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
@Table(name = "programas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Programa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "institucion_id")
    private Institucion institucion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_version_id")
    private PlanVersion planVersion;

    private String nombre;

    public Programa(Institucion institucion, PlanVersion planVersion, String nombre) {
        this.institucion = institucion;
        this.planVersion = planVersion;
        this.nombre = nombre;
    }

    public void adoptar(PlanVersion planVersion) {
        this.planVersion = planVersion;
    }
}
