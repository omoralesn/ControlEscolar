package mx.gob.controlescolar.acceso.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
    private boolean bloqueado = false;
    private boolean credencialesVigentes = true;
    private int intentosFallidos;
    private LocalDate vigenteDesde;
    private LocalDate vigenteHasta;

    @Transient
    private Set<String> permisos = Set.of();

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

    public static Usuario tutor(Institucion institucion, String login, String clave, String nombre) {
        Usuario usuario = new Usuario(institucion, login, clave, nombre);
        usuario.alcance = Alcance.TUTOR;
        return usuario;
    }

    public void cambiarClave(String clave) {
        this.clave = clave;
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

    public boolean esTutor() {
        return alcance == Alcance.TUTOR;
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

    public void bloquear() {
        this.bloqueado = true;
    }

    public void desbloquear() {
        this.bloqueado = false;
    }

    public void vencerCredenciales() {
        this.credencialesVigentes = false;
    }

    public void reponerCredenciales(String claveCifrada) {
        this.clave = claveCifrada;
        this.credencialesVigentes = true;
        this.intentosFallidos = 0;
    }

    public void registrarIntentoFallido() {
        this.intentosFallidos++;
        if (this.intentosFallidos >= 5) {
            this.bloqueado = true;
        }
    }

    public void reiniciarIntentos() {
        this.intentosFallidos = 0;
    }

    /** Permisos del perfil, se cargan al autenticar y pasan a ser autoridades de Spring Security. */
    public void conceder(Set<String> permisos) {
        this.permisos = permisos == null ? Set.of() : Set.copyOf(permisos);
    }

    public void asignarCentro(CentroTrabajo centroTrabajo) {
        this.centroTrabajo = centroTrabajo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> autoridades = new ArrayList<>();
        autoridades.add(new SimpleGrantedAuthority(switch (alcance) {
            case SUPER -> "ROLE_SUPER";
            case PLATAFORMA -> "ROLE_PLATAFORMA";
            case ESCUELA -> "ROLE_ESCUELA";
            case TUTOR -> "ROLE_TUTOR";
        }));
        for (String permiso : permisos) {
            autoridades.add(new SimpleGrantedAuthority(permiso));
        }
        return autoridades;
    }

    @Override
    public boolean isAccountNonExpired() {
        return vigenteHasta == null || !LocalDate.now().isAfter(vigenteHasta);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !bloqueado;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credencialesVigentes;
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
