# MisFinanzas 💰

**Aplicación web para la gestión de finanzas personales.**

MisFinanzas te permite administrar tus ingresos, gastos y tarjetas de crédito desde un solo lugar, con una visión clara de tu situación financiera mediante indicadores en tiempo real.

> Proyecto full-stack de portafolio desarrollado con **Spring Boot + Angular**, siguiendo **Arquitectura Hexagonal** dentro de un **Monolito Modular**.

---

## Tabla de contenidos

- [Descripción del proyecto](#descripción-del-proyecto)
- [Características](#características)
- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Reglas de negocio](#reglas-de-negocio)
- [API REST](#api-rest)
- [Primeros pasos](#primeros-pasos)
- [Testing](#testing)
- [Roadmap](#roadmap)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Autor](#autor)

---

## Descripción del proyecto

**¿Cuánto dinero tengo realmente disponible? ¿Cuánto debo pagar de mis tarjetas de crédito en el próximo periodo? ¿Y cuánto quedará para el siguiente?**

Estas son algunas de las preguntas que MisFinanzas busca responder.

MisFinanzas es una aplicación web para la gestión de finanzas personales que permite centralizar en un solo lugar los ingresos, gastos y movimientos de tarjetas de crédito, proporcionando una visión clara y actualizada de la situación financiera del usuario.

La aplicación calcula el **saldo disponible** a partir de los **ingresos, gastos y pagos** realizados, mientras que las compras realizadas con tarjetas de crédito se gestionan de forma independiente hasta que se registra su pago.

Para las tarjetas de crédito, el sistema aplica automáticamente las reglas de ciclo de facturación según la fecha de corte de cada tarjeta. De esta forma, una compra puede determinarse como parte del próximo pago o del siguiente periodo, permitiendo al usuario conocer con anticipación sus obligaciones.

Los pagos pueden registrarse de forma parcial o completa, y el sistema mantiene el historial de compras y pagos para calcular continuamente el saldo pendiente de cada tarjeta.

Cada usuario tiene acceso exclusivamente a su propia información financiera.


MisFinanzas tiene dos objetivos principales:

1. Resolver un problema real: facilitar el seguimiento de las finanzas personales y ayudar al usuario a conocer cuánto dinero tiene disponible y cuánto deberá pagar en sus próximos ciclos de tarjeta de crédito.
2. Demostrar capacidades técnicas: servir como proyecto de portafolio para aplicar conocimientos de desarrollo Full Stack, arquitectura de software y diseño de reglas de negocio utilizando **Java, Spring Boot, Angular, PostgreSQL, Arquitectura Hexagonal y Monolito Modular.**

---

## Características

### Autenticación y usuarios
- Registro con correo electrónico y contraseña.
- Inicio de sesión seguro con **JWT** (access + refresh token).
- Contraseñas cifradas con **BCrypt**.
- Aislamiento total de datos por usuario.

### Dashboard
- **Saldo disponible** calculado en tiempo real.
- Ingresos y gastos del mes.
- Saldo pendiente de tarjetas de crédito.
- Próximo pago de tarjeta (fecha y días restantes).
- Flujo de caja mensual (chart).
- Últimos movimientos recientes.

### Transacciones
- CRUD de **ingresos** y **gastos** con descripción, valor, categoría y fecha.
- Categorías separadas para ingresos y gastos.

### Tarjetas de crédito
- Registro y administración de tarjetas (banco, cupo, fecha de corte y pago).
- Registro de **compras** y **pagos** (parciales o completos).
- Asignación automática de cada compra al **ciclo de facturación** correspondiente.
- Historial de actividad por tarjeta.
- Cálculo automático del saldo pendiente y porcentaje de uso del cupo.

---

## Stack tecnológico

### Backend

| Tecnología | Detalle |
|---|---|
| Java | 21 |
| Spring Boot | 4.1 |
| Spring Security | Autenticación y autorización con JWT |
| Spring Data JPA | Persistencia |
| Hibernate | ORM |
| Flyway | Migraciones de base de datos |
| PostgreSQL | Base de datos |
| JWT (jjwt) | Tokens de acceso y refresco |
| Lombok | Reducción de código boilerplate |
| Maven | Gestión de dependencias y build |
| JUnit 5 / Mockito | Testing |

### Frontend

| Tecnología | Detalle |
|---|---|
| Angular | 21 (standalone components) |
| TypeScript | 5.9 |
| Tailwind CSS | 4 |
| Angular Router | Lazy loading y guards |
| Reactive Forms | Formularios reactivos |
| Angular HttpClient | Consumo de la API REST |
| RxJS | Programación reactiva |
| Vitest | Testing |

### Infraestructura

| Tecnología | Detalle |
|---|---|
| Docker Compose | Orquestación local (PostgreSQL + backend) |
| Dockerfile multi-stage | Build optimizado del backend |

---

### Capas

- **Domain**: entidades, value objects, puertos (interfaces), servicios y excepciones de dominio. No depende de Spring ni de JPA.
- **Application**: casos de uso (cada uno representa una acción del negocio), DTOs, mappers y validaciones.
- **Infrastructure**: controllers, configuración de Spring, adaptadores, persistencia y seguridad. Toda dependencia tecnológica pertenece a esta capa.

### Módulos

| Módulo | Responsabilidad |
|---|---|
| `auth` | Registro, login, refresh token, JWT y seguridad |
| `user` | Información del usuario |
| `income` | Administración de ingresos |
| `expense` | Administración de gastos |
| `category` | Categorías de ingresos y gastos |
| `creditcards` | Tarjetas, compras y pagos |
| `dashboard` | Indicadores financieros en tiempo real |
| `shared` | Configuración transversal |

---

## Reglas de negocio

### Saldo disponible

```
Saldo disponible = Ingresos − Gastos − Pagos realizados a tarjetas de crédito
```

Las compras realizadas con tarjeta de crédito **no afectan** el saldo disponible hasta que se registra el pago correspondiente.

### Saldo pendiente de tarjetas

```
Saldo pendiente = Compras registradas − Pagos registrados
```

El valor original de una compra nunca se modifica; el historial financiero se conserva por completo. Los pagos pueden ser parciales o completos.

### Ciclos de facturación

El sistema determina automáticamente el ciclo de facturación al que pertenece cada compra, según la **fecha de corte** configurada para la tarjeta.

### Historial

El sistema nunca elimina movimientos financieros por defecto. En caso de requerirse, se prioriza la **eliminación lógica** (soft delete).

---

## API REST

La API sigue convenciones REST y responde siempre mediante **DTOs** (nunca entidades JPA). Endpoints principales:

### Autenticación — `/api/auth`

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/register` | Registro de usuario |
| `POST` | `/login` | Inicio de sesión |
| `POST` | `/refresh` | Renovación de tokens |
| `POST` | `/logout` | Cierre de sesión |

### Ingresos — `/api/incomes`

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/` | Crear ingreso |
| `GET` | `/` | Listar ingresos |
| `GET` | `/{id}` | Obtener ingreso |
| `PUT` | `/{id}` | Actualizar ingreso |
| `DELETE` | `/{id}` | Eliminar ingreso |

### Gastos — `/api/expenses`

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/` | Crear gasto |
| `GET` | `/` | Listar gastos |
| `GET` | `/{id}` | Obtener gasto |
| `PUT` | `/{id}` | Actualizar gasto |
| `DELETE` | `/{id}` | Eliminar gasto |

### Tarjetas de crédito — `/api/credit-cards`

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/` | Crear tarjeta |
| `GET` | `/` | Listar tarjetas |
| `GET` | `/{id}` | Obtener tarjeta |
| `PUT` | `/{id}` | Actualizar tarjeta |
| `DELETE` | `/{id}` | Eliminar tarjeta |
| `GET` | `/{id}/activity` | Actividad de la tarjeta |
| `POST` | `/{id}/purchases` | Registrar compra |
| `POST` | `/{id}/payments` | Registrar pago |

### Categorías — `/api/categories`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/` | Listar categorías |

### Dashboard — `/api/dashboard`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/` | Resumen financiero en tiempo real |

---

## Primeros pasos

### Prerrequisitos

- **Java 21**
- **Node.js** (con npm) para el frontend
- **Docker** y **Docker Compose** (recomendado para la base de datos)
- **Maven** (o usar el wrapper `mvnw` incluido)

### 1. Variables de entorno

Configura las siguientes variables (el proyecto incluye valores por defecto para desarrollo):

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_USERNAME` | Usuario de PostgreSQL | `misfinanzas` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | `misfinanzas` |
| `DB_NAME` | Nombre de la base de datos | `misfinanzas` |
| `JWT_SECRET` | Secreto para firmar los JWT | `dev-secret-change-me-in-production` |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos | `http://localhost:4200` |

> ⚠️ **Importante:** en producción debes cambiar `JWT_SECRET` por un valor seguro.

### 2. Arranque con Docker Compose (backend + base de datos)

```bash
docker-compose up --build
```

- Backend disponible en `http://localhost:8080`
- PostgreSQL en el puerto `5433`

### 3. Arranque manual

**Backend:**

```bash
cd Back/MisFinanzas
./mvnw spring-boot:run
```

**Frontend:**

```bash
cd Front/MisFinanzasFront
npm install
npm start
```

La aplicación estará disponible en `http://localhost:4200`.

---

## Testing

### Backend

```bash
cd Back/MisFinanzas
./mvnw test
```

El backend cuenta con **16 archivos de test** que cubren casos de uso, controllers y adaptadores (incluye `@SpringBootTest` de contexto, JUnit 5 y Mockito).

### Frontend

```bash
cd Front/MisFinanzasFront
npm test
```

Los tests del frontend se ejecutan con **Vitest**.

---

## Roadmap

- [ ] Módulo de **Reportes** (reservado para futuras funcionalidades)
- [ ] Notificaciones y recordatorios de pagos
- [ ] Exportación de movimientos (CSV / PDF)
- [ ] Presupuestos por categoría

---

## Estructura del proyecto

```text
MisFinanzas/
├── Back/
│   └── MisFinanzas/                # Backend Spring Boot
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/aejimenezdev/misfinanzas/
│       │   │   │   ├── auth/            # Módulo de autenticación
│       │   │   │   ├── user/            # Módulo de usuarios
│       │   │   │   ├── income/          # Módulo de ingresos
│       │   │   │   ├── expense/         # Módulo de gastos
│       │   │   │   ├── category/        # Módulo de categorías
│       │   │   │   ├── creditcards/     # Módulo de tarjetas de crédito
│       │   │   │   ├── dashboard/       # Módulo de dashboard
│       │   │   │   └── shared/          # Configuración transversal
│       │   │   └── resources/
│       │   │       ├── db/migration/    # Migraciones Flyway (V1–V6)
│       │   │       └── application*.yml
│       │   └── test/                    # Tests backend (JUnit 5 / Mockito)
│       ├── Dockerfile
│       └── pom.xml
│
├── Front/
│   └── MisFinanzasFront/           # Frontend Angular
│       └── src/app/
│           ├── layout/              # App shell y navegación
│           ├── features/
│           │   ├── auth/            # Login, registro, guards, interceptors
│           │   ├── dashboard/       # Panel con indicadores financieros
│           │   ├── transactions/    # Ingresos y gastos
│           │   └── credit-cards/    # Tarjetas, compras y pagos
│           └── shared/              # Utilidades y directivas
│
├── docker-compose.yml               # PostgreSQL + backend
└── AGENT.md                         # Documentación técnica del proyecto
```

---

## Autor

Desarrollado por **Álvaro Jiménez** ([@aejimenez19](https://github.com/aejimenez19)).
