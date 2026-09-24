# Expediente del alumno y apoyo 911

## Qué se captura

El expediente reúne lo que básica pide en la ficha del alumno y alimenta el resumen 911 de la escuela.

| Bloque | Campos |
|---|---|
| Identidad | CURP, nombre, apellidos, sexo, fecha de nacimiento |
| Apoyos | Usa lentes, zapato ortopédico |
| Discapacidad | Hasta tres del catálogo (motriz, visual, auditiva, intelectual, psicosocial, múltiple, otra) |
| Domicilio | Calle, números, entre calles, colonia, C.P., referencia, estado, municipio, localidad |
| Responsable | CURP, nombre, parentesco, teléfonos, correo, estudios, ocupación, vive con el alumno, domicilio |

Pantalla: `/alumnos/{id}/expediente`. Enlace desde el listado de alumnos.

## Apoyo 911

`/estadistica/911` agrega, por escuela: alumnos registrados, inscripciones activas, activos, bajas, traslados, egresados, con discapacidad y usan lentes. Incluye nombre y CCT. No sustituye el envío oficial del formato 911.

## Código

| Pieza | Archivo |
|---|---|
| Migración | `V12__expediente_alumno.sql` |
| Servicio | `personas/aplicacion/ExpedienteService.java` |
| 911 | `personas/aplicacion/Apoyo911Service.java` |
| Pantallas | `templates/personas/expediente.html`, `apoyo911.html` |
