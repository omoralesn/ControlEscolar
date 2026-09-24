package mx.gob.controlescolar.comun.aplicacion;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import lombok.Generated;
import mx.gob.controlescolar.academico.aplicacion.CalendarioService;
import mx.gob.controlescolar.academico.aplicacion.PlanService;
import mx.gob.controlescolar.academico.aplicacion.ProgramaService;
import mx.gob.controlescolar.academico.dominio.CatalogoPlanesSep;
import mx.gob.controlescolar.academico.dominio.PlanVersion;
import mx.gob.controlescolar.academico.dominio.Programa;
import mx.gob.controlescolar.academico.dominio.TipoMomento;
import mx.gob.controlescolar.acceso.aplicacion.EscuelaService;
import mx.gob.controlescolar.acceso.dominio.Institucion;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.acceso.persistencia.UsuarioRepositorio;
import mx.gob.controlescolar.comun.dominio.Estado;
import mx.gob.controlescolar.comun.dominio.Localidad;
import mx.gob.controlescolar.comun.dominio.Municipio;
import mx.gob.controlescolar.comun.persistencia.EstadoRepositorio;
import mx.gob.controlescolar.comun.persistencia.LocalidadRepositorio;
import mx.gob.controlescolar.comun.persistencia.MunicipioRepositorio;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile(value={"!test"})
public class CargaInicial
implements ApplicationRunner {
    private final UsuarioRepositorio usuarios;
    private final PasswordEncoder encoder;
    private final PlanService planes;
    private final EscuelaService escuelas;
    private final ProgramaService programas;
    private final CalendarioService calendarios;
    private final EstadoRepositorio estados;
    private final MunicipioRepositorio municipios;
    private final LocalidadRepositorio localidades;

    public void run(ApplicationArguments args) {
        if (this.estados.count() == 0L) {
            Estado edomex = (Estado)this.estados.save(new Estado("Estado de M\u00e9xico"));
            Municipio toluca = (Municipio)this.municipios.save(new Municipio(edomex, "Toluca"));
            this.localidades.save(new Localidad(toluca, "Centro"));
        }
        this.asegurarPlanesGenerales();
        this.calendarios.sembrarBasica2025();
        if (this.usuarios.count() == 0L) {
            this.usuarios.save(new Usuario(null, "admin", this.encoder.encode((CharSequence)"admin"), "Administrador de plataforma"));
            PlanVersion primaria = this.planes.buscarVigente("PRIMARIA", "Primaria").orElseThrow();
            Institucion escuela = this.escuelas.alta("Primaria Particular de prueba", null, true, EnumSet.of(Modulo.PLANES, new Modulo[]{Modulo.ALUMNOS, Modulo.PLANTILLA, Modulo.INSCRIPCION, Modulo.EVALUACION, Modulo.DOCUMENTOS}), "particular", "particular");
            Programa programa = this.programas.adoptar(escuela.getId(), primaria.getId(), "Primaria");
            this.programas.anexar(escuela.getId(), programa.getId(), "ROB", "Rob\u00f3tica");
            this.programas.anexar(escuela.getId(), programa.getId(), "DEP", "Deportes");
        }
        if (this.usuarios.findByLogin("super").isEmpty()) {
            this.usuarios.save(Usuario.superusuario("super", this.encoder.encode((CharSequence)"super"), "Superusuario de acceso"));
        }
    }

    private void asegurarPlanesGenerales() {
        this.asegurar(CatalogoPlanesSep.PREESCOLAR, 5, "bimestre", null, null, null, true, true, null, List.of(new PlanService.DefinicionMomento("Observaci\u00f3n", TipoMomento.OBSERVACION)));
        List<PlanService.DefinicionMomento> basica = List.of(new PlanService.DefinicionMomento("Parcial", TipoMomento.ORDINARIO), new PlanService.DefinicionMomento("Regularizaci\u00f3n", TipoMomento.EXTRAORDINARIO), new PlanService.DefinicionMomento("Recuperaci\u00f3n", TipoMomento.EXTRAORDINARIO));
        this.asegurar(CatalogoPlanesSep.PRIMARIA, 5, "bimestre", BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("6"), false, false, 2, basica);
        this.asegurar(CatalogoPlanesSep.SECUNDARIA, 5, "bimestre", BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("6"), false, false, 2, basica);
        this.asegurar(CatalogoPlanesSep.MEDIA_SUPERIOR, 6, "semestre", BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("6"), false, false, null, List.of(new PlanService.DefinicionMomento("Primer ordinario", TipoMomento.ORDINARIO), new PlanService.DefinicionMomento("Segundo ordinario", TipoMomento.ORDINARIO), new PlanService.DefinicionMomento("Promedio", TipoMomento.PROMEDIO), new PlanService.DefinicionMomento("Extraordinario", TipoMomento.EXTRAORDINARIO)));
        this.asegurar(CatalogoPlanesSep.SUPERIOR, 3, "cuatrimestre", BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal("7"), false, false, null, List.of(new PlanService.DefinicionMomento("Primer examen", TipoMomento.ORDINARIO), new PlanService.DefinicionMomento("Segundo examen", TipoMomento.ORDINARIO), new PlanService.DefinicionMomento("Tercer examen", TipoMomento.ORDINARIO), new PlanService.DefinicionMomento("Segundo curso", TipoMomento.SEGUNDO_CURSO), new PlanService.DefinicionMomento("T\u00edtulo", TipoMomento.TITULO)));
    }

    private void asegurar(CatalogoPlanesSep.PlanGeneral general, int cantidad, String unidad, BigDecimal minimo, BigDecimal maximo, BigDecimal aprobatoria, boolean cualitativa, boolean reinscribeSinPromedio, Integer maxReprobadas, List<PlanService.DefinicionMomento> momentos) {
        PlanVersion version = this.planes.buscarVigente(general.nivel(), general.nombre()).orElseGet(() -> this.planes.publicarPlanGeneral(general, minimo, maximo, aprobatoria, cualitativa, reinscribeSinPromedio, maxReprobadas, momentos));
        if (this.planes.periodosDe(version.getId()).isEmpty()) {
            for (int orden = 1; orden <= cantidad; ++orden) {
                this.planes.agregarPeriodo(version.getId(), orden, orden + " " + unidad);
            }
        }
        this.planes.alinearOficiales(version.getId(), general);
    }

    @Generated
    public CargaInicial(UsuarioRepositorio usuarios, PasswordEncoder encoder, PlanService planes, EscuelaService escuelas, ProgramaService programas, CalendarioService calendarios, EstadoRepositorio estados, MunicipioRepositorio municipios, LocalidadRepositorio localidades) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.planes = planes;
        this.escuelas = escuelas;
        this.programas = programas;
        this.calendarios = calendarios;
        this.estados = estados;
        this.municipios = municipios;
        this.localidades = localidades;
    }
}
