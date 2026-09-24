package mx.gob.controlescolar.personas.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mx.gob.controlescolar.comun.dominio.Estado;
import mx.gob.controlescolar.comun.dominio.Localidad;
import mx.gob.controlescolar.comun.dominio.Municipio;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "domicilios")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Domicilio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String calle;
    @Setter
    private String numeroExterior;
    @Setter
    private String numeroInterior;
    @Setter
    private String entreCalle;
    @Setter
    private String yCalle;
    @Setter
    private String colonia;
    @Setter
    private String codigoPostal;
    @Setter
    private String referencia;

    @ManyToOne
    @JoinColumn(name = "localidad_id")
    @Setter
    private Localidad localidad;

    @ManyToOne
    @JoinColumn(name = "municipio_id")
    @Setter
    private Municipio municipio;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    @Setter
    private Estado estado;

    public Domicilio(String calle, String numeroExterior, String colonia, String codigoPostal,
                     Localidad localidad, Municipio municipio, Estado estado) {
        this.calle = calle;
        this.numeroExterior = numeroExterior;
        this.colonia = colonia;
        this.codigoPostal = codigoPostal;
        this.localidad = localidad;
        this.municipio = municipio;
        this.estado = estado;
    }
}
