package mx.gob.controlescolar.personas.aplicacion;

import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.comun.dominio.Estado;
import mx.gob.controlescolar.comun.dominio.Localidad;
import mx.gob.controlescolar.comun.dominio.Municipio;
import mx.gob.controlescolar.comun.persistencia.EstadoRepositorio;
import mx.gob.controlescolar.comun.persistencia.LocalidadRepositorio;
import mx.gob.controlescolar.comun.persistencia.MunicipioRepositorio;
import mx.gob.controlescolar.personas.dominio.Alumno;
import mx.gob.controlescolar.personas.dominio.AlumnoDiscapacidad;
import mx.gob.controlescolar.personas.dominio.AlumnoTutor;
import mx.gob.controlescolar.personas.dominio.Discapacidad;
import mx.gob.controlescolar.personas.dominio.Domicilio;
import mx.gob.controlescolar.personas.dominio.Tutor;
import mx.gob.controlescolar.personas.persistencia.AlumnoDiscapacidadRepositorio;
import mx.gob.controlescolar.personas.persistencia.AlumnoRepositorio;
import mx.gob.controlescolar.personas.persistencia.AlumnoTutorRepositorio;
import mx.gob.controlescolar.personas.persistencia.DiscapacidadRepositorio;
import mx.gob.controlescolar.personas.persistencia.DomicilioRepositorio;
import mx.gob.controlescolar.personas.persistencia.TutorRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExpedienteService {

    private final AlumnoRepositorio alumnos;
    private final TutorRepositorio tutores;
    private final AlumnoTutorRepositorio vinculos;
    private final DomicilioRepositorio domicilios;
    private final DiscapacidadRepositorio discapacidades;
    private final AlumnoDiscapacidadRepositorio alumnoDiscapacidades;
    private final EstadoRepositorio estados;
    private final MunicipioRepositorio municipios;
    private final LocalidadRepositorio localidades;
    private final ModuloGuardia modulos;

    public List<Discapacidad> catalogoDiscapacidades() {
        asegurarCatalogo();
        return discapacidades.findAll();
    }

    public Expediente consultar(Long institucionId, Long alumnoId) {
        modulos.exigir(institucionId, Modulo.ALUMNOS);
        Alumno alumno = deLaEscuela(institucionId, alumnoId);
        List<Discapacidad> delAlumno = alumnoDiscapacidades.findByAlumnoId(alumnoId).stream()
                .map(enlace -> discapacidades.findById(enlace.getDiscapacidadId()).orElseThrow())
                .toList();
        List<Tutor> responsables = new ArrayList<>();
        for (AlumnoTutor vinculo : vinculos.findByAlumnoId(alumnoId)) {
            tutores.findById(vinculo.getTutorId()).ifPresent(responsables::add);
        }
        return new Expediente(alumno, delAlumno, responsables);
    }

    @Transactional
    public Alumno guardarDatos(Long institucionId, Long alumnoId, String nombre, String apellidoPaterno,
                               String apellidoMaterno, String sexo, LocalDate fechaNacimiento,
                               boolean usaLentes, boolean usaZapatoOrtopedico, Set<Long> discapacidadIds) {
        modulos.exigir(institucionId, Modulo.ALUMNOS);
        Alumno alumno = deLaEscuela(institucionId, alumnoId);
        alumno.actualizarIdentidad(nombre, apellidoPaterno, apellidoMaterno, sexo, fechaNacimiento);
        alumno.actualizarApoyos(usaLentes, usaZapatoOrtopedico);
        alumnoDiscapacidades.deleteByAlumnoId(alumnoId);
        if (discapacidadIds != null) {
            Set<Long> unicas = new HashSet<>(discapacidadIds);
            if (unicas.size() > 3) {
                throw new NegocioException("Solo se registran hasta tres discapacidades");
            }
            for (Long discapacidadId : unicas) {
                discapacidades.findById(discapacidadId)
                        .orElseThrow(() -> new NegocioException("La discapacidad no existe"));
                alumnoDiscapacidades.save(new AlumnoDiscapacidad(alumnoId, discapacidadId));
            }
        }
        return alumno;
    }

    @Transactional
    public Domicilio guardarDomicilioAlumno(Long institucionId, Long alumnoId, DatosDomicilio datos) {
        modulos.exigir(institucionId, Modulo.ALUMNOS);
        Alumno alumno = deLaEscuela(institucionId, alumnoId);
        Domicilio domicilio = alumno.getDomicilio() == null
                ? domicilios.save(new Domicilio(null, null, null, null, null, null, null))
                : alumno.getDomicilio();
        aplicar(domicilio, datos);
        alumno.setDomicilio(domicilios.save(domicilio));
        if (domicilio.getLocalidad() != null) {
            alumno.setLocalidad(domicilio.getLocalidad());
        }
        return domicilio;
    }

    @Transactional
    public Tutor guardarResponsable(Long institucionId, Long alumnoId, DatosResponsable datos) {
        modulos.exigir(institucionId, Modulo.ALUMNOS);
        Alumno alumno = deLaEscuela(institucionId, alumnoId);
        Tutor tutor = vinculos.findByAlumnoId(alumnoId).stream()
                .filter(AlumnoTutor::isResponsable)
                .findFirst()
                .flatMap(vinculo -> tutores.findById(vinculo.getTutorId()))
                .orElseGet(() -> tutores.save(new Tutor(institucionId, datos.nombre(), null)));
        tutor.actualizar(datos.curp(), datos.nombre(), datos.apellidoPaterno(), datos.apellidoMaterno(),
                datos.parentesco(), datos.telefono(), datos.celular(), datos.correo(),
                datos.estudios(), datos.ocupacion(), datos.viveConAlumno());
        if (datos.domicilio() != null) {
            Domicilio domicilio = tutor.getDomicilio() == null
                    ? domicilios.save(new Domicilio(null, null, null, null, null, null, null))
                    : tutor.getDomicilio();
            aplicar(domicilio, datos.domicilio());
            tutor.setDomicilio(domicilios.save(domicilio));
        }
        Tutor guardado = tutores.save(tutor);
        boolean ligado = vinculos.findByAlumnoId(alumnoId).stream()
                .anyMatch(vinculo -> vinculo.getTutorId().equals(guardado.getId()));
        if (!ligado) {
            vinculos.save(new AlumnoTutor(alumno.getId(), guardado.getId(), true));
        }
        return guardado;
    }

    private void aplicar(Domicilio domicilio, DatosDomicilio datos) {
        Estado estado = datos.estadoId() == null ? null : estados.findById(datos.estadoId())
                .orElseThrow(() -> new NegocioException("El estado no existe"));
        Municipio municipio = datos.municipioId() == null ? null : municipios.findById(datos.municipioId())
                .orElseThrow(() -> new NegocioException("El municipio no existe"));
        Localidad localidad = datos.localidadId() == null ? null : localidades.findById(datos.localidadId())
                .orElseThrow(() -> new NegocioException("La localidad no existe"));
        domicilio.setCalle(datos.calle());
        domicilio.setNumeroExterior(datos.numeroExterior());
        domicilio.setNumeroInterior(datos.numeroInterior());
        domicilio.setEntreCalle(datos.entreCalle());
        domicilio.setYCalle(datos.yCalle());
        domicilio.setColonia(datos.colonia());
        domicilio.setCodigoPostal(datos.codigoPostal());
        domicilio.setReferencia(datos.referencia());
        domicilio.setEstado(estado);
        domicilio.setMunicipio(municipio);
        domicilio.setLocalidad(localidad);
    }

    private Alumno deLaEscuela(Long institucionId, Long alumnoId) {
        Alumno alumno = alumnos.findById(alumnoId)
                .orElseThrow(() -> new NegocioException("El alumno no existe"));
        if (!alumno.getInstitucion().getId().equals(institucionId)) {
            throw new NegocioException("El alumno no pertenece a la escuela");
        }
        return alumno;
    }

    private void asegurarCatalogo() {
        if (discapacidades.count() > 0) {
            return;
        }
        discapacidades.save(new Discapacidad("MOTORA", "Motriz"));
        discapacidades.save(new Discapacidad("VISUAL", "Visual"));
        discapacidades.save(new Discapacidad("AUDITIVA", "Auditiva"));
        discapacidades.save(new Discapacidad("INTELECTUAL", "Intelectual"));
        discapacidades.save(new Discapacidad("PSICOSOCIAL", "Psicosocial"));
        discapacidades.save(new Discapacidad("MULTIPLE", "Múltiple"));
        discapacidades.save(new Discapacidad("OTRA", "Otra"));
    }

    public record Expediente(Alumno alumno, List<Discapacidad> discapacidades, List<Tutor> responsables) {
    }

    public record DatosDomicilio(String calle, String numeroExterior, String numeroInterior, String entreCalle,
                                 String yCalle, String colonia, String codigoPostal, String referencia,
                                 Long estadoId, Long municipioId, Long localidadId) {
    }

    public record DatosResponsable(String curp, String nombre, String apellidoPaterno, String apellidoMaterno,
                                   String parentesco, String telefono, String celular, String correo,
                                   String estudios, String ocupacion, boolean viveConAlumno,
                                   DatosDomicilio domicilio) {
    }
}
