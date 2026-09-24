package mx.gob.controlescolar.acceso.dominio;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "institucion_id")
    private Institucion institucion;

    @ManyToOne
    @JoinColumn(name = "centro_trabajo_id")
    private CentroTrabajo centroTrabajo;

    private String login;
    private String clave;
    private String nombre;

    @Enumerated(EnumType.STRING)
    private Alcance alcance;

    private boolean activo = true;
    private LocalDate vigenteDesde;
    private LocalDate vigenteHasta;

    public Usuario(Institucion institucion, String login, String clave, String nombre) {
        this.institucion = institucion;
        this.login = login;
        this.clave = clave;
        this.nombre = nombre;
        this.alcance = institucion == null ? Alcance.PLATAFORMA : Alcance.ESCUELA;
        this.activo = true;
    }

    public static Usuario superusuario(String login, String clave, String nombre) {
        Usuario usuario = new Usuario(null, login, clave, nombre);
        usuario.alcance = Alcance.SUPER;
        return usuario;
    }

    public boolean esPlataforma() {
        return alcance == Alcance.PLATAFORMA;
    }

    public boolean esSuper() {
        return alcance == Alcance.SUPER;
    }

    public boolean esEscuela() {
        return alcance == Alcance.ESCUELA;
    }

    public void definirVigencia(LocalDate desde, LocalDate hasta) {
        this.vigenteDesde = desde;
        this.vigenteHasta = hasta;
    }

    public void suspenderCuenta() {
        this.activo = false;
    }

    public void activarCuenta() {
        this.activo = true;
    }

    public void asignarCentro(CentroTrabajo centroTrabajo) {
        this.centroTrabajo = centroTrabajo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String rol = switch (alcance) {
            case SUPER -> "ROLE_SUPER";
            case PLATAFORMA -> "ROLE_PLATAFORMA";
            case ESCUELA -> "ROLE_ESCUELA";
        };
        return List.of(new SimpleGrantedAuthority(rol));
    }

    @Override
    public boolean isEnabled() {
        if (!activo) {
            return false;
        }
        LocalDate hoy = LocalDate.now();
        if (vigenteDesde != null && hoy.isBefore(vigenteDesde)) {
            return false;
        }
        if (vigenteHasta != null && hoy.isAfter(vigenteHasta)) {
            return false;
        }
        if (institucion != null && !institucion.isActiva()) {
            return false;
        }
        return institucion == null || institucion.getPlantel() == null || institucion.getPlantel().isActivo();
    }

    @Override
    public String getPassword() {
        return clave;
    }

    @Override
    public String getUsername() {
        return login;
    }
}
