# Control escolar

Un solo WAR (Spring Boot 3, Java 17) para WildFly, con PostgreSQL y MongoDB.

## Arranque

```bash
mvn -DskipTests package
docker compose up --build
```

Entrada: `http://localhost:8080/entrar`  
Salud: `http://localhost:8080/actuator/health`

| Usuario | Clave | Rol |
|---|---|---|
| `admin` | `admin` | Plataforma (planes y calendarios SEP) |
| `super` | `super` | Acceso (CCT, plantel, módulos, vigencia) |
| `particular` | `particular` | Escuela de prueba |

Las opciones de diseño, datos y pruebas por etapa están en `docs/mesas/decisiones.md`.
