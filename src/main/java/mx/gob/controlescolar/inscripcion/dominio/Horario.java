package mx.gob.controlescolar.inscripcion.dominio;

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
@Table(name = "horarios")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long institucionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    private String asignaturaClave;

    @ManyToOne
    @JoinColumn(name = "asignacion_id")
    private AsignacionDocente asignacion;

    private String dia;
    private String horaInicio;
    private String horaFin;

    public Horario(Long institucionId, Grupo grupo, Profesor profesor, String asignaturaClave,
                   String dia, String horaInicio, String horaFin) {
        this.institucionId = institucionId;
        this.grupo = grupo;
        this.profesor = profesor;
        this.asignaturaClave = asignaturaClave;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public void ligar(AsignacionDocente asignacion) {
        this.asignacion = asignacion;
    }
}
