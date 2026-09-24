package mx.gob.controlescolar.academico.aplicacion;

import java.util.List;
import lombok.Generated;
import mx.gob.controlescolar.academico.dominio.AnexoVersion;
import mx.gob.controlescolar.academico.dominio.AsignaturaAnexo;
import mx.gob.controlescolar.academico.dominio.EstadoVersion;
import mx.gob.controlescolar.academico.dominio.PlanVersion;
import mx.gob.controlescolar.academico.dominio.Programa;
import mx.gob.controlescolar.academico.persistencia.AnexoRepositorio;
import mx.gob.controlescolar.academico.persistencia.AsignaturaAnexoRepositorio;
import mx.gob.controlescolar.academico.persistencia.PlanVersionRepositorio;
import mx.gob.controlescolar.academico.persistencia.ProgramaRepositorio;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.CentroTrabajo;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.persistencia.CentroTrabajoRepositorio;
import mx.gob.controlescolar.acceso.persistencia.InstitucionRepositorio;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.evaluacion.persistencia.CalificacionRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgramaService {
    private final ProgramaRepositorio programas;
    private final PlanVersionRepositorio versiones;
    private final InstitucionRepositorio instituciones;
    private final AnexoRepositorio anexos;
    private final AsignaturaAnexoRepositorio asignaturasAnexo;
    private final CalificacionRepositorio calificaciones;
    private final CentroTrabajoRepositorio centros;
    private final ModuloGuardia modulos;

    @Transactional
    public Programa adoptar(Long institucionId, Long planVersionId, String nombre) {
        this.modulos.exigir(institucionId, Modulo.PLANES);
        Institucion institucion = (Institucion)this.instituciones.findById(institucionId).orElseThrow(() -> new NegocioException("La escuela no existe"));
        PlanVersion version = (PlanVersion)this.versiones.findById(planVersionId).orElseThrow(() -> new NegocioException("El plan no existe"));
        List<CentroTrabajo> centrosEscuela = this.centros.findByInstitucionId(institucionId);
        if (!centrosEscuela.isEmpty() && centrosEscuela.stream().noneMatch(centro -> centro.getNivel().equals(version.getPlanRector().getNivel()))) {
            throw new NegocioException("La CCT de la escuela no corresponde al nivel del plan");
        }
        return (Programa)this.programas.save(new Programa(institucion, version, nombre));
    }

    @Transactional
    public AsignaturaAnexo anexar(Long institucionId, Long programaId, String clave, String nombre) {
        this.modulos.exigir(institucionId, Modulo.PLANES);
        Programa programa = (Programa)this.programas.findById(programaId).orElseThrow(() -> new NegocioException("El programa no existe"));
        if (!programa.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El programa no pertenece a la escuela");
        }
        if (!programa.getInstitucion().isParticular()) {
            throw new NegocioException("Solo una escuela particular anexa materias");
        }
        AnexoVersion anexo = this.anexos.findByProgramaIdAndEstado(programaId, EstadoVersion.VIGENTE).orElseGet(() -> (AnexoVersion)this.anexos.save(new AnexoVersion(programa.getInstitucion(), programa, 1, EstadoVersion.VIGENTE)));
        if (this.calificaciones.existsByPlanVersionIdAndComplementariaTrue(programa.getPlanVersion().getId())) {
            int siguiente = anexo.getNumero() + 1;
            anexo.cerrar();
            anexo = (AnexoVersion)this.anexos.save(new AnexoVersion(programa.getInstitucion(), programa, siguiente, EstadoVersion.VIGENTE));
        }
        return (AsignaturaAnexo)this.asignaturasAnexo.save(new AsignaturaAnexo(anexo, clave, nombre));
    }

    public List<Programa> deLaEscuela(Long institucionId) {
        return this.programas.findByInstitucionId(institucionId);
    }

    public List<AsignaturaAnexo> materiasAnexo(Long anexoVersionId) {
        return this.asignaturasAnexo.findByAnexoVersionId(anexoVersionId);
    }

    @Generated
    public ProgramaService(ProgramaRepositorio programas, PlanVersionRepositorio versiones, InstitucionRepositorio instituciones, AnexoRepositorio anexos, AsignaturaAnexoRepositorio asignaturasAnexo, CalificacionRepositorio calificaciones, CentroTrabajoRepositorio centros, ModuloGuardia modulos) {
        this.programas = programas;
        this.versiones = versiones;
        this.instituciones = instituciones;
        this.anexos = anexos;
        this.asignaturasAnexo = asignaturasAnexo;
        this.calificaciones = calificaciones;
        this.centros = centros;
        this.modulos = modulos;
    }
}
