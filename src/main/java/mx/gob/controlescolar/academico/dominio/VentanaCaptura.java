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
import mx.gob.controlescolar.academico.dominio.MomentoEvaluacion;
import mx.gob.controlescolar.academico.dominio.PeriodoCiclo;

@Entity
@Table(name="ventanas_captura")
public class VentanaCaptura {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false)
    @JoinColumn(name="periodo_ciclo_id")
    private PeriodoCiclo periodoCiclo;
    @ManyToOne(optional=false)
    @JoinColumn(name="momento_evaluacion_id")
    private MomentoEvaluacion momento;
    private LocalDate capturaDesde;
    private LocalDate capturaHasta;
    private LocalDate publicacionDesde;

    public VentanaCaptura(PeriodoCiclo periodoCiclo, MomentoEvaluacion momento, LocalDate capturaDesde, LocalDate capturaHasta, LocalDate publicacionDesde) {
        this.periodoCiclo = periodoCiclo;
        this.momento = momento;
        this.capturaDesde = capturaDesde;
        this.capturaHasta = capturaHasta;
        this.publicacionDesde = publicacionDesde;
    }

    public void publicarSiFalta(LocalDate hoy) {
        if (this.publicacionDesde == null) {
            this.publicacionDesde = hoy;
        }
    }

    public boolean permiteCaptura(LocalDate hoy) {
        return this.periodoCiclo.abierto() && !hoy.isBefore(this.capturaDesde) && !hoy.isAfter(this.capturaHasta);
    }

    public boolean publicada(LocalDate hoy) {
        return !this.periodoCiclo.abierto() || this.publicacionDesde != null && !hoy.isBefore(this.publicacionDesde);
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public PeriodoCiclo getPeriodoCiclo() {
        return this.periodoCiclo;
    }

    @Generated
    public MomentoEvaluacion getMomento() {
        return this.momento;
    }

    @Generated
    public LocalDate getCapturaDesde() {
        return this.capturaDesde;
    }

    @Generated
    public LocalDate getCapturaHasta() {
        return this.capturaHasta;
    }

    @Generated
    public LocalDate getPublicacionDesde() {
        return this.publicacionDesde;
    }

    @Generated
    protected VentanaCaptura() {
    }
}
