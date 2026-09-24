package mx.gob.controlescolar.academico.dominio;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ciclos_escolares")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CicloEscolar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private LocalDate inicio;
    private LocalDate fin;

    public CicloEscolar(String nombre, LocalDate inicio, LocalDate fin) {
        this.nombre = nombre;
        this.inicio = inicio;
        this.fin = fin;
    }
}
