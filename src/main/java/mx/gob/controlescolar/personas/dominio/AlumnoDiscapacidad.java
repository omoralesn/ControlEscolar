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

import java.io.Serializable;

@Entity
@Table(name = "alumnos_discapacidades")
@IdClass(AlumnoDiscapacidad.Clave.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlumnoDiscapacidad {

    @Id
    private Long alumnoId;

    @Id
    private Long discapacidadId;

    public AlumnoDiscapacidad(Long alumnoId, Long discapacidadId) {
        this.alumnoId = alumnoId;
        this.discapacidadId = discapacidadId;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Clave implements Serializable {
        private Long alumnoId;
        private Long discapacidadId;
    }
}
