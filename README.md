# XploreNow – Backend API

Backend REST API para la aplicación Android **XploreNow** (turismo/actividades).  
Stack: **Spring Boot 4.0.3 · Java 17 · SQL Server · Azure App Service**

---

## Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Java JDK | 17 |
| Maven | 3.9+ (incluido via `mvnw`) |
| Docker & Docker Compose | Para SQL Server local |

---

## Variables de entorno / Credenciales

> **⚠️ Las credenciales fueron removidas del código fuente por seguridad.**  
> Cada entorno debe configurarlas por separado.

### 1. Base de datos (SQL Server)

| Variable | Descripción | Ejemplo |
|---|---|---|
| `SPRING_DATASOURCE_URL` | JDBC connection string | `jdbc:sqlserver://localhost:1433;databaseName=desa_db;encrypt=false;trustServerCertificate=true` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la DB (solo local/Docker) | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la DB (solo local/Docker) | `YourStrong!Passw0rd` |

**En Azure:** usar `authentication=ActiveDirectoryManagedIdentity` en la URL y **no** definir username/password.

### 2. Email (SMTP para OTP)

| Variable | Descripción | Ejemplo |
|---|---|---|
| `MAIL_HOST` | Servidor SMTP | `smtp.gmail.com` |
| `MAIL_PORT` | Puerto SMTP | `587` |
| `MAIL_USERNAME` | Email de envío | `tu-email@gmail.com` |
| `MAIL_PASSWORD` | App password de Gmail | *(generar en Google Account → Security → App Passwords)* |
| `MAIL_FROM` | Dirección "From" del email (en Gmail, usar el mismo correo o un alias verificado) | `tu-email@gmail.com` |
| `MAIL_FAIL_FAST` | Si el correo falla, corta el flujo con error explícito | `true` |

> En desarrollo local con Docker Compose, el backend usa MailHog automáticamente.
> Para correrlo fuera de Docker, creá `src/main/resources/application-local.properties`
> o definí las variables SMTP manualmente.

### 3. JWT

| Variable | Descripción | Ejemplo |
|---|---|---|
| `JWT_SECRET` | Clave secreta (mín. 32 bytes) | `MyVerySecureSecretKeyForJWT32ByteMinimumRequired` |
| `JWT_EXPIRATION` | Duración del token en ms | `86400000` (24 horas) |

---

## Desarrollo local

### Opción A: Docker Compose (recomendado)

```bash
# 1. Levantar SQL Server
docker compose up -d sqlserver sqlserver-init

# 2. Correr la app con perfil local
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

El perfil `local` usa `application-local.properties` (excluido del repo) que contiene las credenciales de la DB local.

### Opción B: Docker Compose completo

```bash
docker compose up --build
```

Esto levanta SQL Server + la app Spring Boot juntos.
También levanta **MailHog** para SMTP local y activa el perfil `local`, por lo que el flujo OTP funciona sin credenciales reales.

UI de MailHog:

```bash
http://localhost:8025
```

Ahí podés ver los mails OTP enviados por el backend.

### Crear `application-local.properties`

Este archivo está en `.gitignore`. Crear manualmente en `src/main/resources/`:

```properties
# ── Perfil LOCAL: SQL Server con user/password ───────
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=desa_db;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourStrong!Passw0rd

# ── Email (SMTP) ────────────────────────────────────
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password

# ── JWT ──────────────────────────────────────────────
jwt.secret=MyVerySecureSecretKeyForJWT32ByteMinimumRequired
```

---

## Seed de datos

La inicialización (`data.sql`) está deshabilitada por defecto (`spring.sql.init.mode=never`).  
Hibernate crea las tablas automáticamente con `ddl-auto=update`.

Para cargar datos de prueba en una DB vacía:

```bash
# Opción 1: cambiar temporalmente en application.properties
spring.sql.init.mode=always

# Opción 2: ejecutar data.sql manualmente contra la DB
sqlcmd -S localhost -U sa -P "YourStrong!Passw0rd" -d desa_db -i src/main/resources/data.sql
```

**Usuarios de prueba** (password: `123456`):
- `test@example.com` (id=1)
- `maria@example.com` (id=2)

---

## Deploy en Azure App Service

### Application Settings requeridos

Configurar en **Azure Portal → App Service → Configuration → Application Settings**:

```
SPRING_DATASOURCE_URL = jdbc:sqlserver://<server>.database.windows.net:1433;database=<db>;encrypt=true;authentication=ActiveDirectoryManagedIdentity
MAIL_USERNAME = <email>
MAIL_PASSWORD = <app-password>
MAIL_FROM = noreply@xplorenow.com
JWT_SECRET = <clave-secreta-produccion>
```

> **No** agregar `SPRING_DATASOURCE_USERNAME` ni `SPRING_DATASOURCE_PASSWORD` en Azure  
> (la autenticación es por Managed Identity).

---

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Registro clásico |
| `POST` | `/api/v1/auth/login` | Login → JWT |
| `POST` | `/api/v1/auth/signup/otp/request` | Solicitar OTP de registro |
| `POST` | `/api/v1/auth/signup/otp/complete` | Completar registro con OTP |
| `POST` | `/api/v1/auth/password-reset/request` | Solicitar reset de password |
| `GET` | `/api/v1/destinations` | Listar destinos |
| `GET` | `/api/v1/categories` | Listar categorías |
| `GET` | `/api/v1/activities` | Catálogo de actividades (paginado, filtros) |
| `GET` | `/api/v1/activities/featured` | Actividades destacadas |
| `GET` | `/api/v1/activities/recommended` | Recomendadas por preferencias |
| `GET` | `/api/v1/activities/{id}` | Detalle de actividad |
| `GET` | `/api/v1/users/{id}/profile` | Ver perfil |
| `PUT` | `/api/v1/users/{id}/profile` | Editar perfil y preferencias |
| `POST` | `/api/v1/users/{id}/bookings` | Crear reserva |
| `DELETE` | `/api/v1/users/{id}/bookings/{bookingId}` | Cancelar reserva |
| `GET` | `/api/v1/users/{id}/bookings` | Mis actividades (filtro por status) |
| `GET` | `/api/v1/users/{id}/history` | Historial (filtros fecha/destino) |
| `POST` | `/api/v1/users/{id}/reviews` | Dejar reseña (48h post-actividad) |
| `GET` | `/api/v1/users/{id}/reviews/booking/{bookingId}` | Ver reseña de un booking |

---

## Build

```bash
# Compilar y generar JAR
./mvnw clean package -DskipTests

# El JAR queda en target/desa-apps-1-tp-backend-0.0.1-SNAPSHOT.jar
java -jar target/desa-apps-1-tp-backend-0.0.1-SNAPSHOT.jar
```
