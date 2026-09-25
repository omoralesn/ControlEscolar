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

## Depurar con Eclipse

Un solo WildFly: el de **Docker**. Eclipse **no** arranca servidor; solo se engancha al depurador en el puerto **8787**.

**No mezclar:** si Docker tiene `8080`/`8787`, no des **Start** ni **Debug** al WildFly de la vista *Servers* de Eclipse (ese es otro proceso y choca de puertos).

### 1. Arrancar la app (solo Docker)

```bash
mvn -DskipTests package
docker compose up --build
```

Espera a que responda `http://localhost:8080/actuator/health` (UP) y a que `docker compose ps` muestre `8080:8080` y `8787:8787`.

En Eclipse deja el servidor de *Servers* en **Stopped**.

### 2. Conectar Eclipse (solo attach)

1. Abre el proyecto `ControlEscolar` en Eclipse.
2. **Run → Debug Configurations…**
3. **Remote Java Application** → `ControlEscolar-WildFly-Remote`  
   (si no aparece: New → Project `ControlEscolar`, Connection Type **Standard (Socket Attach)**, Host `localhost`, Port `8787`).
4. Pon breakpoints en el código fuente.
5. Pulsa **Debug** en esa configuración remota (insecto).  
   Eso **no** levanta WildFly: solo se conecta al que ya corre en Docker.
6. Usa la app en el navegador: `http://localhost:8080/entrar`. Al disparar el breakpoint, Eclipse se detiene.

### Si no conecta o dice puerto en uso

- ¿Hay un WildFly en *Servers* de Eclipse en verde? → **Stop** y vuelve a intentar el Remote Debug.
- `docker compose ps` debe mostrar `0.0.0.0:8787->8787/tcp`.
- Reinicia solo el contenedor: `docker compose up -d --build wildfly`.
- Para liberar todo Docker: `docker compose down` y vuelve al paso 1.
