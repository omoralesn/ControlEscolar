-- Las tablas nombran el conjunto (plural, snake_case).
-- La columna de llave foránea sigue en singular: apunta a un solo registro.
-- Si el volumen ya quedó en plural, cada renombre se omite.

DO $$
BEGIN
    IF to_regclass('public.institucion') IS NOT NULL THEN
        ALTER TABLE institucion RENAME TO instituciones;
    END IF;
    IF to_regclass('public.usuario') IS NOT NULL THEN
        ALTER TABLE usuario RENAME TO usuarios;
    END IF;
    IF to_regclass('public.perfil') IS NOT NULL THEN
        ALTER TABLE perfil RENAME TO perfiles;
    END IF;
    IF to_regclass('public.permiso') IS NOT NULL THEN
        ALTER TABLE permiso RENAME TO permisos;
    END IF;
    IF to_regclass('public.perfil_permiso') IS NOT NULL THEN
        ALTER TABLE perfil_permiso RENAME TO perfiles_permisos;
    END IF;
    IF to_regclass('public.usuario_perfil') IS NOT NULL THEN
        ALTER TABLE usuario_perfil RENAME TO usuarios_perfiles;
    END IF;
    IF to_regclass('public.institucion_modulo') IS NOT NULL THEN
        ALTER TABLE institucion_modulo RENAME TO instituciones_modulos;
    END IF;

    IF to_regclass('public.plan_rector') IS NOT NULL THEN
        ALTER TABLE plan_rector RENAME TO planes_rectores;
    END IF;
    IF to_regclass('public.plan_version') IS NOT NULL THEN
        ALTER TABLE plan_version RENAME TO planes_versiones;
    END IF;
    IF to_regclass('public.periodo_plan') IS NOT NULL THEN
        ALTER TABLE periodo_plan RENAME TO periodos_planes;
    END IF;
    IF to_regclass('public.momento_evaluacion') IS NOT NULL THEN
        ALTER TABLE momento_evaluacion RENAME TO momentos_evaluacion;
    END IF;
    IF to_regclass('public.asignatura_plan') IS NOT NULL THEN
        ALTER TABLE asignatura_plan RENAME TO asignaturas_planes;
    END IF;
    IF to_regclass('public.programa') IS NOT NULL THEN
        ALTER TABLE programa RENAME TO programas;
    END IF;
    IF to_regclass('public.anexo_version') IS NOT NULL THEN
        ALTER TABLE anexo_version RENAME TO anexos_versiones;
    END IF;
    IF to_regclass('public.asignatura_anexo') IS NOT NULL THEN
        ALTER TABLE asignatura_anexo RENAME TO asignaturas_anexos;
    END IF;

    IF to_regclass('public.profesor') IS NOT NULL THEN
        ALTER TABLE profesor RENAME TO profesores;
    END IF;
    IF to_regclass('public.grupo') IS NOT NULL THEN
        ALTER TABLE grupo RENAME TO grupos;
    END IF;
    IF to_regclass('public.horario') IS NOT NULL THEN
        ALTER TABLE horario RENAME TO horarios;
    END IF;

    IF to_regclass('public.estado') IS NOT NULL THEN
        ALTER TABLE estado RENAME TO estados;
    END IF;
    IF to_regclass('public.municipio') IS NOT NULL THEN
        ALTER TABLE municipio RENAME TO municipios;
    END IF;
    IF to_regclass('public.localidad') IS NOT NULL THEN
        ALTER TABLE localidad RENAME TO localidades;
    END IF;
    IF to_regclass('public.alumno') IS NOT NULL THEN
        ALTER TABLE alumno RENAME TO alumnos;
    END IF;
    IF to_regclass('public.tutor') IS NOT NULL THEN
        ALTER TABLE tutor RENAME TO tutores;
    END IF;
    IF to_regclass('public.alumno_tutor') IS NOT NULL THEN
        ALTER TABLE alumno_tutor RENAME TO alumnos_tutores;
    END IF;
    IF to_regclass('public.inscripcion') IS NOT NULL THEN
        ALTER TABLE inscripcion RENAME TO inscripciones;
    END IF;
    IF to_regclass('public.movimiento_alumno') IS NOT NULL THEN
        ALTER TABLE movimiento_alumno RENAME TO movimientos_alumnos;
    END IF;

    IF to_regclass('public.calificacion') IS NOT NULL THEN
        ALTER TABLE calificacion RENAME TO calificaciones;
    END IF;

    IF to_regclass('public.lista_asistencia') IS NOT NULL THEN
        ALTER TABLE lista_asistencia RENAME TO listas_asistencia;
    END IF;
    IF to_regclass('public.asistencia_alumno') IS NOT NULL THEN
        ALTER TABLE asistencia_alumno RENAME TO asistencias_alumnos;
    END IF;
    IF to_regclass('public.falta_profesor') IS NOT NULL THEN
        ALTER TABLE falta_profesor RENAME TO faltas_profesores;
    END IF;

    IF to_regclass('public.documento_escolar') IS NOT NULL THEN
        ALTER TABLE documento_escolar RENAME TO documentos_escolares;
    END IF;
    IF to_regclass('public.aviso') IS NOT NULL THEN
        ALTER TABLE aviso RENAME TO avisos;
    END IF;
    IF to_regclass('public.auditoria_evento') IS NOT NULL THEN
        ALTER TABLE auditoria_evento RENAME TO auditorias_eventos;
    END IF;
END $$;
