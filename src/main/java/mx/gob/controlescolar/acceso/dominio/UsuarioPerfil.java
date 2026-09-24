package mx.gob.controlescolar.acceso.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "usuarios_perfiles")
@IdClass(UsuarioPerfil.Clave.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsuarioPerfil {

    @Id
    private Long usuarioId;

    @Id
    private Long perfilId;

    public UsuarioPerfil(Long usuarioId, Long perfilId) {
        this.usuarioId = usuarioId;
        this.perfilId = perfilId;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Clave implements Serializable {
        private Long usuarioId;
        private Long perfilId;
    }
}
