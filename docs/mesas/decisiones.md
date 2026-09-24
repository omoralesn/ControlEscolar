# Decisiones de las mesas

Un solo WAR. Estas son las opciones elegidas para cada etapa. El artefacto usa Java 17, Spring Boot 3.4.5 y WildFly 35.0.1.Final. Donde la mesa de desarrollo propuso otra versión o librería, el apartado lo deja escrito y el código se quedó con lo ya cerrado en el plan y en las otras mesas.

## Desarrollo

La mesa fijó, al revisar versiones el 22 de septiembre de 2026, Spring Boot 3.5.16 y WildFly EE 10 41.0.1.Final, con la imagen `quay.io/wildfly/wildfly-ee-10:41.0.1.Final-jdk21`. El WAR seguiría compilado en Java 17. Esa subida no se aplicó: el plan ya cerró Boot 3, Java 17 y WildFly 31 o superior, y el artefacto corre en Boot 3.4.5 sobre `quay.io/wildfly/wildfly:35.0.1.Final-jdk17`.

| Etapa | Opción de la mesa | Qué quedó en el artefacto |
| --- | --- | --- |
| 1. Cimiento | WAR, Tomcat embebido en `provided`, datasource JNDI en WildFly y Flyway al arranque. | WAR con Tomcat `provided` y Flyway. El datasource lo define Spring por perfil, no JNDI. |
| 2. Acceso | Spring Security 6 con sesión HTTP y un filtro `TenantContext` por escuela. | Sesión HTTP. Cada servicio filtra por `institucion_id`. Sin JWT. |
| 3. Plan y anexo | `PlanEstudio` y `PlanEstudioVersion`, con el anexo en JSONB. | Tablas de plan, versión, periodo, asignatura y anexo. La mesa de base de datos ya había dejado el anexo en tablas. |
| 4. Grupos | Agregado de grupo y validación de cruces de horario en el dominio. | El servicio rechaza el horario que se empalma para el mismo profesor y día. |
| 5. Alumnos | Historial append-only de movimientos; el estado se deriva del último movimiento. | Cada baja, reinscripción, traslado, repetidor o cambio deja un movimiento. El estatus actual también vive en el alumno. |
| 6. Calificaciones | Strategy por esquema: numérico, letra o competencia. | El esquema es un dato de la versión: escala, momentos y si la evaluación es cualitativa. No hay una clase por nivel. |
| 7. Asistencia | Bitácora diaria con clave única por sujeto, fecha y contexto, y carga por lote. | Lista del día del alumno y falta del profesor en tablas distintas. |
| 8. Documentos | OpenHTMLToPDF desde Thymeleaf y el binario en GridFS. | OpenPDF y GridFS. En pruebas el archivo queda en memoria. Sin firma ni sello. |
| 9. Padres | Perfil de Spring y rol `PADRE`. | Módulo `PADRES` por escuela. Un perfil de Spring lo encendería para todo el WAR. |
| 10. Producción | Actuator, Prometheus, healthchecks de Compose, secretos por entorno y proxy TLS. | Health de Actuator, Compose, respaldo, ensayo y auditoría. Prometheus y el proxy TLS quedan para cuando haya un entorno público. |

## Diseño

Una plantilla Thymeleaf (`comun/layout.html`) con cabecera, menú lateral y cuerpo. La tipografía es la del sistema, igual para todas las escuelas. Un módulo apagado no sale en el menú ni tiene pantalla; si se entra por la URL, la respuesta es que no está disponible. El PDF es un solo formato, con el nombre y los datos de la escuela. La plantilla no calcula promedios ni promoción.

| Etapa | Patrón |
| --- | --- |
| 1. Cimiento | Pantalla de ejemplo que fija cabecera, menú y cuerpo. |
| 2. Acceso | Formulario de entrada, alta o suspensión de escuela y activación de módulos. |
| 3. Plan rector y anexo | Formulario de la versión (borrador, vigente, cerrado) y consulta de solo lectura de la vigente. |
| 4. Grupos y horarios | Tabla de grupos y profesores, con grilla de horario. |
| 5. Alumnos y movimientos | Ficha del alumno, listado por grupo y acciones de movimiento sobre la ficha. |
| 6. Calificaciones | Tabla editable alumno por materia por momento. La vista envía datos y no calcula. |
| 7. Asistencia | Lista del día por grupo y consulta del histórico. |
| 8. Documentos | Consulta con filtro, vista previa y descarga del PDF de la escuela. |
| 9. Portal del tutor | Solo lectura de evaluaciones, avisos y avance de los hijos, si el módulo está activo. |
| 10. Producción | No se diseñan pantallas de título, sello, firma ni de módulos apagados. |

## Base de datos

PostgreSQL 16 para los datos. MongoDB 7 guarda los PDF en GridFS. En PostgreSQL quedan el folio, la fecha y el identificador del archivo. Flyway es la única cadena de cambios. Toda tabla de negocio lleva `institucion_id`. No hay columnas fijas de calificación.

| Etapa | Tablas y regla |
| --- | --- |
| 1. Cimiento | Sin tablas de negocio. Solo el historial de Flyway. |
| 2. Acceso | `institucion`, `institucion_modulo`, `usuario`, `perfil`, `permiso`, `perfil_permiso`, `usuario_perfil`. El usuario de escuela exige institución. El administrador de plataforma no. Módulo apagado: operación denegada. |
| 3. Académico | Plan rector, anexo, programa, periodo y esquema, cada uno con versión en borrador, vigente o cerrado. La escuela adopta el plan; no lo edita. Una versión cerrada no se modifica. |
| 4. Grupos | `profesor`, `grupo`, `grupo_asignatura`, `asignacion_docente`, `horario`. El grupo apunta a la versión del programa. |
| 5. Alumnos | `alumno`, `tutor`, `alumno_tutor`, `inscripcion`, `movimiento_alumno`. La inscripción fija la versión del plan. Un cambio de plan no reescribe el historial. |
| 6. Calificaciones | Una fila en `calificacion` por alumno, asignatura, periodo y momento. Si ya hay una calificación, esa versión del plan no se edita. |
| 7. Asistencia | `lista_asistencia`, `asistencia_alumno` y `falta_profesor`. Las faltas del profesor van aparte. |
| 8. Documentos | `documento_escolar` con tipo boleta, historial o constancia. El archivo vive en GridFS. La constancia lista solo el plan rector. Sin firma ni sello. |
| 9. Padres | Avisos ligados al tutor y a sus hijos. Solo si el módulo está activo en la escuela. Sin correo ni mensaje de texto. |
| 10. Producción | `auditoria_evento` y registro de respaldos. El ensayo restaura en otro entorno. |

## Pruebas

JUnit 5. La regla se prueba en el servicio, no en la plantilla. MockMvc cubre la pantalla de ejemplo. Desde la etapa 2, PostgreSQL corre en Testcontainers. No hay pruebas de navegador en esta fase.

| Etapa | Prueba que la cierra |
| --- | --- |
| 1. Cimiento | La pantalla de ejemplo responde 200. |
| 2. Acceso | Con el módulo apagado, el servicio niega la operación aunque el perfil la tenga. |
| 3. Plan | Si hay una calificación, el servicio rechaza editar esa versión. |
| 4. Grupos | El grupo se guarda y se consulta por escuela. El de otra escuela no aparece. |
| 5. Alumnos | Al cambiar de plan, las calificaciones del plan anterior siguen en su versión. |
| 6. Calificaciones | Un esquema con un número de momentos distinto de seis acepta solo esos momentos. |
| 7. Asistencia | Una falta de alumno no crea ni cambia una falta de profesor. |
| 8. Documentos | El PDF sale con datos de la escuela y sin sello ni firma. |
| 9. Padres | Con el módulo apagado no hay consulta. Con el módulo activo, el tutor solo ve a sus hijos. |
| 10. Producción | Checklist: respaldo restaurable, auditoría, ambiente de ensayo y alta de escuela antes de capturar. |
