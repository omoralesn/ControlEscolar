package mx.gob.controlescolar.asistencia.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "faltas_profesores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FaltaProfesor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long institucionId;
    private Long profesorId;
    private LocalDate fecha;

    public FaltaProfesor(Long institucionId, Long profesorId, LocalDate fecha) {
        this.institucionId = institucionId;
        this.profesorId = profesorId;
        this.fecha = fecha;
    }
}
