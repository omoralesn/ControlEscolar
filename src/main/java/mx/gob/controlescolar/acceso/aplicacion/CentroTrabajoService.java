package mx.gob.controlescolar.acceso.aplicacion;

import mx.gob.controlescolar.acceso.dominio.CentroTrabajo;
import mx.gob.controlescolar.acceso.dominio.ClaveCentroTrabajo;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Plantel;
import mx.gob.controlescolar.acceso.persistencia.CentroTrabajoRepositorio;
import mx.gob.controlescolar.acceso.persistencia.InstitucionRepositorio;
import mx.gob.controlescolar.acceso.persistencia.PlantelRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CentroTrabajoService {

    private final CentroTrabajoRepositorio centros;
    private final PlantelRepositorio planteles;
    private final InstitucionRepositorio instituciones;

    @Transactional
    public CentroTrabajo registrar(Long institucionId, String clave, String sostenimiento, String nombre) {
        Institucion institucion = instituciones.findById(institucionId)
                .orElseThrow(() -> new NegocioException("La escuela no existe"));
        ClaveCentroTrabajo analizada = ClaveCentroTrabajo.analizar(clave, sostenimiento);
        if (centros.findByClave(analizada.texto()).isPresent()) {
            throw new NegocioException("Esa CCT ya está registrada");
        }
        Plantel plantel = institucion.getPlantel();
        if (plantel == null) {
            plantel = planteles.save(new Plantel(institucion.getNombre()));
            institucion.asignarPlantel(plantel);
        }
        institucion.definirNivel(analizada.nivel());
        return centros.save(new CentroTrabajo(institucion, plantel, nombre, analizada));
    }

    @Transactional
    public void compartirPlantel(Long institucionId, Long plantelId) {
        Institucion institucion = instituciones.findById(institucionId).orElseThrow();
        institucion.asignarPlantel(planteles.findById(plantelId).orElseThrow());
    }

    public List<CentroTrabajo> deLaEscuela(Long institucionId) {
        return centros.findByInstitucionId(institucionId);
    }

    public List<CentroTrabajo> delPlantel(Long institucionId) {
        Institucion institucion = instituciones.findById(institucionId).orElseThrow();
        if (institucion.getPlantel() == null) {
            return centros.findByInstitucionId(institucionId);
        }
        return centros.findByPlantelId(institucion.getPlantel().getId());
    }
}
