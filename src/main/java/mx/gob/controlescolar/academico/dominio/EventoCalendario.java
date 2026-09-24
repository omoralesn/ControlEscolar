package mx.gob.controlescolar.academico.dominio;

import java.time.LocalDate;

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
@Table(name = "eventos_calendario")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventoCalendario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "calendario_id")
    private CalendarioEscolar calendario;

    private String tipo;
    private String nombre;
    private LocalDate inicio;
    private LocalDate fin;

    public EventoCalendario(CalendarioEscolar calendario, String tipo, String nombre, LocalDate inicio, LocalDate fin) {
        this.calendario = calendario;
        this.tipo = tipo;
        this.nombre = nombre;
        this.inicio = inicio;
        this.fin = fin;
    }
}
