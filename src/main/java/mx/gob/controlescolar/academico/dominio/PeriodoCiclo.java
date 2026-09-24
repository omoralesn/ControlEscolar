package mx.gob.controlescolar.academico.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Generated;
import mx.gob.controlescolar.academico.dominio.CalendarioEscolar;
import mx.gob.controlescolar.academico.dominio.PeriodoPlan;

@Entity
@Table(name="periodos_ciclo")
public class PeriodoCiclo {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false)
    @JoinColumn(name="calendario_id")
    private CalendarioEscolar calendario;
    @ManyToOne(optional=false)
    @JoinColumn(name="periodo_plan_id")
    private PeriodoPlan periodoPlan;
    private int orden;
    private LocalDate inicio;
    private LocalDate fin;
    private String estado;

    public PeriodoCiclo(CalendarioEscolar calendario, PeriodoPlan periodoPlan, LocalDate inicio, LocalDate fin) {
        this.calendario = calendario;
        this.periodoPlan = periodoPlan;
        this.orden = periodoPlan.getOrden();
        this.inicio = inicio;
        this.fin = fin;
        this.estado = "ABIERTO";
    }

    public void cerrar() {
        this.estado = "CERRADO";
    }

    public boolean abierto() {
        return "ABIERTO".equals(this.estado);
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public CalendarioEscolar getCalendario() {
        return this.calendario;
    }

    @Generated
    public PeriodoPlan getPeriodoPlan() {
        return this.periodoPlan;
    }

    @Generated
    public int getOrden() {
        return this.orden;
    }

    @Generated
    public LocalDate getInicio() {
        return this.inicio;
    }

    @Generated
    public LocalDate getFin() {
        return this.fin;
    }

    @Generated
    public String getEstado() {
        return this.estado;
    }

    @Generated
    protected PeriodoCiclo() {
    }
}
