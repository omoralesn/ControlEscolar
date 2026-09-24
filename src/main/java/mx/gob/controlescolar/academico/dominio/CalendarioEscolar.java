package mx.gob.controlescolar.academico.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mx.gob.controlescolar.acceso.dominio.Institucion;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "calendarios_escolares")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarioEscolar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ciclo_id")
    private CicloEscolar ciclo;

    private String nivel;

    @ManyToOne
    @JoinColumn(name = "institucion_id")
    private Institucion institucion;

    private Integer diasEfectivos;
    private String estado;

    public CalendarioEscolar(CicloEscolar ciclo, String nivel, Institucion institucion, Integer diasEfectivos) {
        this.ciclo = ciclo;
        this.nivel = nivel;
        this.institucion = institucion;
        this.diasEfectivos = diasEfectivos;
        this.estado = "PUBLICADO";
    }

    public boolean oficial() {
        return institucion == null;
    }
}
