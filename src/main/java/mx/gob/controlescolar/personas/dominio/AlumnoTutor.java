package mx.gob.controlescolar.personas.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "alumnos_tutores")
@IdClass(AlumnoTutor.Clave.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlumnoTutor {

    @Id
    private Long alumnoId;

    @Id
    private Long tutorId;

    @Setter
    private boolean responsable = true;

    public AlumnoTutor(Long alumnoId, Long tutorId) {
        this(alumnoId, tutorId, true);
    }

    public AlumnoTutor(Long alumnoId, Long tutorId, boolean responsable) {
        this.alumnoId = alumnoId;
        this.tutorId = tutorId;
        this.responsable = responsable;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Clave implements Serializable {
        private Long alumnoId;
        private Long tutorId;
    }
}
