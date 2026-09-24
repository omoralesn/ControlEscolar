package mx.gob.controlescolar.personas.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mx.gob.controlescolar.academico.dominio.PlanVersion;
import mx.gob.controlescolar.inscripcion.dominio.Grupo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inscripciones")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long institucionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alumno_id")
    private Alumno alumno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_version_id")
    private PlanVersion planVersion;

    private Long anexoVersionId;
    private boolean historica;

    public Inscripcion(Long institucionId, Alumno alumno, Grupo grupo, PlanVersion planVersion, Long anexoVersionId) {
        this.institucionId = institucionId;
        this.alumno = alumno;
        this.grupo = grupo;
        this.planVersion = planVersion;
        this.anexoVersionId = anexoVersionId;
        this.historica = false;
    }

    public void marcarHistorica() {
        this.historica = true;
    }

    public void cambiarGrupo(Grupo grupo) {
        this.grupo = grupo;
    }
}
