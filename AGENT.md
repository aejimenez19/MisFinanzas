# AGENT.md

# Personal Finance Web App

## Descripción del Proyecto

Este proyecto consiste en el desarrollo de una aplicación web para la gestión de finanzas personales.

El objetivo es permitir que cada usuario administre sus ingresos, gastos y tarjetas de crédito, proporcionando una visión clara de su situación financiera mediante indicadores y reportes.

Este proyecto tiene un doble propósito:

1. Desarrollar una aplicación que pueda ser utilizada en un entorno real.
2. Servir como proyecto de portafolio para demostrar conocimientos en desarrollo Full Stack, Arquitectura Hexagonal, Java, Spring Boot y Angular.

---

# Stack Tecnológico

## Frontend

* Angular
* TypeScript
* Tailwind CSS
* Angular Router
* Angular Reactive Forms
* Angular HttpClient

## Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* PostgreSQL
* Flyway
* JWT Authentication
* Maven

---

# Arquitectura General

## Monolito Modular

Este proyecto **NO utilizará microservicios**.

Toda la aplicación será desarrollada como un **Monolito Modular**, donde todos los módulos se ejecutan dentro de una única aplicación Spring Boot y comparten una única base de datos PostgreSQL.

La arquitectura debe favorecer una futura migración a microservicios, pero **sin introducir complejidad innecesaria desde el inicio**.

No deben implementarse:

* API Gateway
* Service Discovery
* Comunicación entre microservicios
* Mensajería
* Bases de datos separadas

Cada módulo debe mantener un bajo acoplamiento y una alta cohesión.

---

# Arquitectura Interna

Cada módulo seguirá una **Arquitectura Hexagonal (Ports & Adapters).**

Cada módulo estará organizado de la siguiente manera:

```text
module
├── domain
│   ├── model
│   ├── ports
│   ├── services
│   ├── valueobjects
│   └── exceptions
│
├── application
│   ├── usecases
│   ├── dto
│   ├── mapper
│   └── validator
│
└── infrastructure
    ├── controller
    ├── persistence
    ├── adapter
    └── configuration
```

---

# Módulos del Sistema

La aplicación estará organizada por módulos.

## Authentication

Responsable de:

* Registro
* Inicio de sesión
* Refresh Token
* JWT
* Seguridad

---

## Users

Responsable de la información del usuario.

---

## Incomes

Administración de ingresos.

---

## Expenses

Administración de gastos.

---

## Credit Cards

Administración de:

* Tarjetas
* Compras
* Pagos

---

## Dashboard

Obtención de indicadores financieros.

---

## Reports

Módulo reservado para futuras funcionalidades.

---

# Principios de Desarrollo

Todo el código generado deberá respetar:

* SOLID
* Clean Code
* KISS
* DRY
* Separation of Concerns

Evitar sobreingeniería.

Priorizar código mantenible antes que código complejo.

---

# Reglas de Arquitectura

## Domain

La capa Domain únicamente puede contener:

* Entidades
* Value Objects
* Interfaces (Ports)
* Servicios de Dominio
* Excepciones de Dominio

No debe depender de Spring Boot.

No debe depender de JPA.

No debe depender de infraestructura.

---

## Application

La capa Application contiene:

* Casos de uso
* DTOs
* Mappers
* Validaciones de aplicación

Cada caso de uso representa una acción del negocio.

Ejemplos:

* CreateIncome
* RegisterExpense
* RegisterCreditCardPurchase
* RegisterCreditCardPayment

---

## Infrastructure

Contiene:

* Controllers
* Configuración Spring
* Adaptadores
* Persistencia
* Seguridad
* Repositorios

Toda dependencia tecnológica pertenece aquí.

---

# Convenciones

## Idioma

Todo el código debe escribirse en inglés.

Ejemplos:

* User
* Income
* Expense
* CreditCard
* Purchase
* Payment

La documentación puede escribirse en español.

---

## DTOs

Nunca exponer entidades directamente.

Siempre utilizar DTOs.

Ejemplo:

* IncomeRequest
* IncomeResponse
* ExpenseRequest
* CreditCardResponse

---

## Validaciones

Utilizar Bean Validation.

Ejemplo:

* @NotNull
* @Email
* @Positive

---

## Base de Datos

Motor:

PostgreSQL

Las migraciones deberán realizarse mediante Flyway.

No utilizar generación automática del esquema para ambientes productivos.

---

# Autenticación

La autenticación utilizará JWT.

Características:

* Registro por correo electrónico
* Inicio de sesión
* Refresh Token
* BCrypt para contraseñas

Cada usuario únicamente podrá acceder a sus propios datos.

---

# Modelo Funcional

## Usuario

Un usuario puede tener:

* múltiples ingresos
* múltiples gastos
* múltiples tarjetas de crédito

---

## Ingresos

Cada ingreso tendrá:

* descripción
* valor
* categoría
* fecha del movimiento
* fecha de registro

Los ingresos aumentan el saldo disponible.

---

## Gastos

Cada gasto tendrá:

* descripción
* valor
* categoría
* fecha del movimiento
* fecha de registro

Los gastos disminuyen el saldo disponible.

---

## Tarjetas de Crédito

Cada tarjeta tendrá:

* nombre
* banco
* cupo total
* fecha de corte
* fecha de pago
* estado

Un usuario puede registrar múltiples tarjetas.

---

## Compras con Tarjeta

Cada compra tendrá:

* descripción
* valor
* fecha de compra
* tarjeta asociada

Las compras:

* No afectan el saldo disponible.
* Aumentan el saldo pendiente de la tarjeta.

El sistema debe determinar automáticamente a qué ciclo de facturación pertenece cada compra según la fecha de corte configurada para la tarjeta.

---

## Pagos de Tarjeta

Se permiten:

* pagos parciales
* pagos completos

Cada pago tendrá:

* valor
* fecha de pago
* tarjeta asociada

Los pagos:

* disminuyen el saldo pendiente de la tarjeta.
* disminuyen el saldo disponible.

Nunca se eliminará el historial de pagos.

---

# Dashboard

El Dashboard calculará información en tiempo real.

No almacenará valores calculados.

Indicadores iniciales:

* Saldo disponible
* Ingresos del mes
* Gastos del mes
* Saldo pendiente de tarjetas
* Próximo pago de tarjeta
* Últimos movimientos

---

# Reglas de Negocio

## Saldo Disponible

El saldo disponible se calcula mediante la siguiente fórmula:

Saldo Disponible =
Ingresos
− Gastos
− Pagos realizados a tarjetas de crédito

Las compras realizadas con tarjeta de crédito **NO afectan** el saldo disponible hasta que se registre un pago.

---

## Saldo Pendiente de Tarjetas

El saldo pendiente de una tarjeta se calcula como:

Saldo Pendiente =
Compras Registradas
− Pagos Registrados

Nunca se debe modificar el valor original de una compra.

Toda la información debe mantenerse para conservar el historial financiero.

---

## Historial

El sistema nunca eliminará movimientos financieros por defecto.

En caso de requerirse eliminación, se priorizará el uso de eliminación lógica (soft delete).

---

# API REST

La API deberá seguir convenciones REST.

Métodos:

* GET
* POST
* PUT
* PATCH
* DELETE

Utilizar correctamente los códigos HTTP.

No devolver entidades JPA.

Siempre responder mediante DTOs.

---

# Calidad del Código

El código generado deberá:

* Ser reutilizable.
* Evitar duplicidad.
* Favorecer composición sobre herencia.
* Mantener clases pequeñas.
* Mantener métodos pequeños.
* Aplicar inversión de dependencias.
* Mantener responsabilidades bien definidas.

---

# Comportamiento Esperado del Agente IA

Cuando el agente genere código deberá:

* Respetar la arquitectura definida.
* Mantener el proyecto como un Monolito Modular.
* Respetar la Arquitectura Hexagonal.
* No mover lógica de negocio fuera del dominio o los casos de uso.
* No introducir dependencias innecesarias.
* No generar código que rompa las reglas de negocio.
* Proponer mejoras justificadas cuando aporten valor.
* Mantener consistencia en nombres, estructura y estilo.
* Explicar decisiones arquitectónicas relevantes cuando sea necesario.
* Antes de implementar una funcionalidad compleja, verificar que sea coherente con las reglas de negocio y la arquitectura definidas en este documento.

El objetivo principal es construir una aplicación mantenible, escalable y preparada para evolucionar en el futuro sin sacrificar la simplicidad del desarrollo actual.

