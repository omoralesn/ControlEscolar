package mx.gob.controlescolar.acceso.dominio;

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

@Entity
@Table(name = "instituciones_modulos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstitucionModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "institucion_id")
    private Institucion institucion;

    @Enumerated(EnumType.STRING)
    private Modulo modulo;

    public InstitucionModulo(Institucion institucion, Modulo modulo) {
        this.institucion = institucion;
        this.modulo = modulo;
    }
}
