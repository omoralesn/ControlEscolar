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
@Table(name = "asignaciones_docentes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AsignacionDocente {

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

    public AsignacionDocente(Long institucionId, Grupo grupo, Profesor profesor, String asignaturaClave) {
        this.institucionId = institucionId;
        this.grupo = grupo;
        this.profesor = profesor;
        this.asignaturaClave = asignaturaClave;
    }
}
