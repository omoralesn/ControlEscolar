package mx.gob.controlescolar.comun.aplicacion;

import mx.gob.controlescolar.acceso.dominio.Permiso;
import mx.gob.controlescolar.acceso.dominio.PermisoCatalogo;
import mx.gob.controlescolar.acceso.persistencia.PermisoRepositorio;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Profile("test")
@RequiredArgsConstructor
public class PermisosDePrueba implements ApplicationRunner {

    private final PermisoRepositorio permisos;

    @Override
    public void run(ApplicationArguments args) {
        if (permisos.count() > 0) {
            return;
        }
        for (PermisoCatalogo.Def definicion : PermisoCatalogo.TODOS) {
            permisos.save(new Permiso(definicion.codigo()));
        }
    }
}
