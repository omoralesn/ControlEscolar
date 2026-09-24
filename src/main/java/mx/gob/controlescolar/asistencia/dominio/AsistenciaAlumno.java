package mx.gob.controlescolar.asistencia.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asistencias_alumnos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AsistenciaAlumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long listaId;
    private Long alumnoId;
    private boolean presente;

    public AsistenciaAlumno(Long listaId, Long alumnoId, boolean presente) {
        this.listaId = listaId;
        this.alumnoId = alumnoId;
        this.presente = presente;
    }
}
