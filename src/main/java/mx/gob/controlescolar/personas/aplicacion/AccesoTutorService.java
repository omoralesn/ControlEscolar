package mx.gob.controlescolar.personas.aplicacion;

import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.personas.dominio.Alumno;
import mx.gob.controlescolar.personas.dominio.AlumnoTutor;
import mx.gob.controlescolar.personas.dominio.Tutor;
import mx.gob.controlescolar.personas.persistencia.AlumnoRepositorio;
import mx.gob.controlescolar.personas.persistencia.AlumnoTutorRepositorio;
import mx.gob.controlescolar.personas.persistencia.TutorRepositorio;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;

/**
 * El tutor entra con la matrícula del alumno y una clave que solo él conoce.
 * La clave se genera sola o la define la escuela o el tutor, y se puede cambiar después.
 */
@Service
@RequiredArgsConstructor
public class AccesoTutorService {

    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private final AlumnoRepositorio alumnos;
    private final TutorRepositorio tutores;
    private final AlumnoTutorRepositorio vinculos;
    private final UsuarioRepositorio usuarios;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Acceso definir(Long institucionId, Long alumnoId, String claveDeseada) {
        Alumno alumno = alumnos.findById(alumnoId)
                .orElseThrow(() -> new NegocioException("El alumno no existe"));
        if (!alumno.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El alumno no pertenece a la escuela");
        }
        if (alumno.getMatricula() == null || alumno.getMatricula().isBlank()) {
            throw new NegocioException("El alumno no tiene matrícula");
        }
        Tutor tutor = vinculos.findByAlumnoId(alumnoId).stream()
                .filter(AlumnoTutor::isResponsable)
                .findFirst()
                .flatMap(vinculo -> tutores.findById(vinculo.getTutorId()))
                .orElseThrow(() -> new NegocioException("Registre al tutor antes de abrir su acceso"));
        String clave = claveDeseada == null || claveDeseada.isBlank() ? generar() : claveDeseada.trim();
        if (clave.length() < 6) {
            throw new NegocioException("La clave del tutor debe tener al menos 6 caracteres");
        }
        String hash = passwordEncoder.encode(clave);
        Usuario usuario = tutor.getUsuarioId() == null ? null : usuarios.findById(tutor.getUsuarioId()).orElse(null);
        if (usuario == null) {
            if (usuarios.findByLogin(alumno.getMatricula()).isPresent()) {
                throw new NegocioException("Esa matrícula ya tiene un acceso");
            }
            usuario = usuarios.save(Usuario.tutor(alumno.getInstitucion(), alumno.getMatricula(), hash, tutor.nombreCompleto()));
            tutor.setUsuarioId(usuario.getId());
        } else {
            usuario.cambiarClave(hash);
        }
        return new Acceso(alumno.getMatricula(), clave);
    }

    @Transactional
    public void cambiarPropia(Usuario tutorUsuario, String claveNueva) {
        if (tutorUsuario == null || !tutorUsuario.esTutor()) {
            throw new NegocioException("Solo el tutor cambia su clave desde esta pantalla");
        }
        if (claveNueva == null || claveNueva.trim().length() < 6) {
            throw new NegocioException("La clave del tutor debe tener al menos 6 caracteres");
        }
        tutorUsuario.cambiarClave(passwordEncoder.encode(claveNueva.trim()));
    }

    private static String generar() {
        StringBuilder clave = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            clave.append(ALFABETO.charAt(ALEATORIO.nextInt(ALFABETO.length())));
        }
        return clave.toString();
    }

    public record Acceso(String matricula, String clave) {
    }
}
