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
@Table(name = "listas_asistencia")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ListaAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long institucionId;
    private Long grupoId;
    private LocalDate fecha;

    public ListaAsistencia(Long institucionId, Long grupoId, LocalDate fecha) {
        this.institucionId = institucionId;
        this.grupoId = grupoId;
        this.fecha = fecha;
    }
}
