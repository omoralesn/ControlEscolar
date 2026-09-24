package mx.gob.controlescolar.acceso.aplicacion;

import mx.gob.controlescolar.acceso.dominio.Plantel;
import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.persistencia.PlantelRepositorio;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccesoService {

    private final PlantelRepositorio planteles;
    private final UsuarioRepositorio usuarios;

    public List<Plantel> planteles() {
        return planteles.findAll();
    }

    public List<Usuario> deLaEscuela(Long institucionId) {
        return usuarios.findByInstitucionId(institucionId);
    }

    @Transactional
    public void suspenderPlantel(Long plantelId) {
        plantel(plantelId).suspender();
    }

    @Transactional
    public void activarPlantel(Long plantelId) {
        plantel(plantelId).activar();
    }

    @Transactional
    public void definirVigencia(Long usuarioId, LocalDate desde, LocalDate hasta) {
        usuarioDeEscuela(usuarioId).definirVigencia(desde, hasta);
    }

    @Transactional
    public void suspenderUsuario(Long usuarioId) {
        usuarioDeEscuela(usuarioId).suspenderCuenta();
    }

    @Transactional
    public void activarUsuario(Long usuarioId) {
        usuarioDeEscuela(usuarioId).activarCuenta();
    }

    private Plantel plantel(Long plantelId) {
        return planteles.findById(plantelId).orElseThrow(() -> new NegocioException("El plantel no existe"));
    }

    private Usuario usuarioDeEscuela(Long usuarioId) {
        Usuario usuario = usuarios.findById(usuarioId).orElseThrow(() -> new NegocioException("El usuario no existe"));
        if (!usuario.esEscuela()) {
            throw new NegocioException("Solo se administra la vigencia de un usuario de escuela");
        }
        return usuario;
    }
}
