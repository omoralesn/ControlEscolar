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
import mx.gob.controlescolar.acceso.dominio.Institucion;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "anexos_versiones")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnexoVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "institucion_id")
    private Institucion institucion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "programa_id")
    private Programa programa;

    private int numero;

    @Enumerated(EnumType.STRING)
    private EstadoVersion estado;

    public AnexoVersion(Institucion institucion, Programa programa, int numero, EstadoVersion estado) {
        this.institucion = institucion;
        this.programa = programa;
        this.numero = numero;
        this.estado = estado;
    }

    public void cerrar() {
        this.estado = EstadoVersion.CERRADO;
    }
}
