package mx.gob.controlescolar.academico.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "planes_versiones")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_rector_id")
    private PlanRector planRector;

    private int numero;

    @Enumerated(EnumType.STRING)
    private EstadoVersion estado;

    private BigDecimal minimo;
    private BigDecimal maximo;
    @Setter
    private BigDecimal aprobatoria;
    private boolean cualitativa;
    private boolean reinscribeSinPromedio;
    private Integer maxMateriasReprobadas;
    private Integer umbralCreditos;
    private boolean esquemaEditable;

    public PlanVersion(PlanRector planRector, int numero, EstadoVersion estado, BigDecimal minimo,
                       BigDecimal maximo, BigDecimal aprobatoria, boolean cualitativa,
                       boolean reinscribeSinPromedio, Integer maxMateriasReprobadas) {
        this.planRector = planRector;
        this.numero = numero;
        this.estado = estado;
        this.minimo = minimo;
        this.maximo = maximo;
        this.aprobatoria = aprobatoria;
        this.cualitativa = cualitativa;
        this.reinscribeSinPromedio = reinscribeSinPromedio;
        this.maxMateriasReprobadas = maxMateriasReprobadas;
        this.esquemaEditable = true;
    }

    public void bloquearEsquema() {
        this.esquemaEditable = false;
    }

    public void definirUmbralCreditos(Integer umbralCreditos) {
        this.umbralCreditos = umbralCreditos;
    }

    public void copiarReglas(PlanVersion origen) {
        this.minimo = origen.minimo;
        this.maximo = origen.maximo;
        this.cualitativa = origen.cualitativa;
        this.reinscribeSinPromedio = origen.reinscribeSinPromedio;
        this.maxMateriasReprobadas = origen.maxMateriasReprobadas;
        this.umbralCreditos = origen.umbralCreditos;
        this.esquemaEditable = origen.esquemaEditable;
    }

    public void cerrar() {
        this.estado = EstadoVersion.CERRADO;
    }

    public void publicar() {
        this.estado = EstadoVersion.VIGENTE;
    }
}
