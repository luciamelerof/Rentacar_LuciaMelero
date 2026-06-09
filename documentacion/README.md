# GestorAlquiler — RentaCar

**Alumna:** Lucía Melero  
**Módulo:** Programación  
**Curso:** 2025–2026 
**Repositorio:** https://github.com/luciamelerof/Rentacar_LuciaMelero

## Descripción general

GestorAlquiler es una aplicación de escritorio en Java para gestionar una empresa de alquiler de coches. Ha sido desarrollada como proyecto final del módulo de Programación del curso 2025–2026.

Desde la aplicación se puede gestionar todo el negocio: los vehículos de la flota, los clientes y empleados registrados, y los alquileres activos. Además, incluye un conversor de divisas en tiempo real integrado con una API externa (ExchangeRate-API) que muestra el precio de cada vehículo en diferentes monedas.

El sistema distingue dos roles de usuario con permisos diferentes:
- **Empleado:** acceso completo a todos los módulos (Vehículos, Alquileres, Usuarios)
- **Cliente:** puede ver vehículos y su precio en otras monedas, y crear sus propios alquileres

---

## Tecnologías utilizadas

- Java 17
- MySQL 8 con JDBC
- Java Swing para la interfaz gráfica
- Patrón DAO + arquitectura MVC
- ExchangeRate-API para conversión de divisas en tiempo real

---

## Estructura del proyecto

```
src/
  api/    → ExchangeRateService.java (integración con ExchangeRate-API)
  db/     → ConexionDB.java (gestión de la conexión a MySQL)
  model/  → Usuario, Cliente, Empleado, Vehiculo, Alquiler
  dao/    → Interfaces + implementaciones (una por entidad)
  dto/    → AlquilerDTO (para consultas con JOIN)
  view/   → Login.java, Registro.java, VentanaPrincipal.java
  Main.java

lib/           → JARs externos (MySQL Connector)
sql/           → rentacar.sql (script completo con datos de prueba)
documentacion/ → README.md, documentacion.pdf, documentacion.docx
```

---

## Arquitectura MVC

El proyecto sigue el patrón **Modelo - Vista - Controlador**:

- **Model** (`model/`): clases POJO que representan los datos. No contienen lógica SQL ni de interfaz. Incluye herencia con `Usuario` como clase base y `Cliente` y `Empleado` como clases hijas.
- **View** (`view/`): ventanas Swing. No contienen ninguna línea de SQL. Se comunican con la base de datos exclusivamente a través de los DAOs.
- **Controller** (`dao/`): implementaciones DAO que contienen toda la lógica de acceso a datos. Reciben objetos del modelo, ejecutan las operaciones SQL y devuelven objetos del modelo.

---

## Patrón DAO

Cada entidad tiene su interfaz y su implementación:

| Interfaz | Implementación |
|---|---|
| `IUsuarioDAO` | `UsuarioDAOImpl` |
| `IVehiculoDAO` | `VehiculoDAOImpl` |
| `IAlquilerDAO` | `AlquilerDAOImpl` |

Métodos implementados:
- `UsuarioDAO`: `validar()`, `registrarCliente()`, `registrarEmpleado()`, `listarTodos()`, `actualizar()`, `actualizarPassword()`, `eliminar()`
- `VehiculoDAO`: `insertar()`, `actualizar()`, `eliminar()`, `listarTodos()`, `listarDisponibles()`
- `AlquilerDAO`: `insertar()`, `actualizar()`, `eliminar()`, `listarTodos()`, `buscarPorId()`

Buenas prácticas aplicadas:
- `try-with-resources` en todos los bloques JDBC
- `PreparedStatement` siempre, sin concatenar SQL con valores del usuario
- `setAutoCommit(false)` / `commit()` / `rollback()` en todas las operaciones multi-tabla

---

## Base de datos

La base de datos se llama `rentacar` y sigue el patrón **Joined Table Inheritance**:

- `usuarios` es la tabla raíz con los campos comunes (id, username, password, email, nombre, apellidos, dni, rol)
- `clientes` y `empleados` son tablas hijas que amplían `usuarios` mediante FK primaria (`usuario_id`)
- `vehiculos` es la entidad principal del dominio
- `alquileres` es la tabla de relación N:M entre clientes y vehículos, con fecha_inicio, fecha_fin, precio_total y estado

Todas las relaciones usan `ON DELETE CASCADE`. El script completo está en `sql/rentacar.sql`.

### Diagrama de tablas

```
usuarios (id, username, password, email, nombre, apellidos, dni, rol)
    ├── clientes (usuario_id FK, telefono, direccion, carnet_conducir)
    └── empleados (usuario_id FK, salario, fecha_alta)

vehiculos (id, matricula, marca, modelo, anio, categoria, precio_dia, disponible)

alquileres (id, cliente_id FK, vehiculo_id FK, empleado_id FK nullable,
            fecha_inicio, fecha_fin, precio_total, estado)
```

---

## Instrucciones de instalación y ejecución

**Requisitos previos:**
- Java 17 o superior
- MySQL 8
- Visual Studio Code con Extension Pack for Java

**Pasos:**

1. Clona el repositorio o descomprime el ZIP del proyecto
2. Abre MySQL y ejecuta el script `sql/rentacar.sql` para crear la base de datos con los datos de prueba
3. Abre `src/db/ConexionDB.java` y ajusta `USER` y `PASS` con tus credenciales de MySQL si son diferentes
4. Abre la carpeta del proyecto en VS Code
5. Pulsa F5 o ejecuta `Main.java` para arrancar la aplicación

**Usuarios de prueba** (contraseña `1234` para todos):

| Usuario | Rol | Nombre |
|---------|-----|--------|
| admin | empleado | Carlos López |
| laura | empleado | Laura Sánchez |
| maria | cliente | María García |
| juan | cliente | Juan Martínez |

---

## Funcionalidades por módulo

### Login
Valida usuario y contraseña contra la base de datos. Si son correctos abre el dashboard. Permite navegar al Registro para crear una cuenta nueva.

### Registro
Formulario dinámico que muestra u oculta campos según el rol elegido en el desplegable (cliente o empleado). Usa una transacción atómica para insertar en `usuarios` y en la tabla hija a la vez, con rollback automático si algo falla.

### Vehículos
- **Empleado:** CRUD completo (crear, editar, eliminar vehículos)
- **Cliente:** solo lectura
- **Ambos:** conversor de divisas en tiempo real (ver más abajo)

### Alquileres
- **Empleado:** CRUD completo, puede asignar cualquier cliente y empleado
- **Cliente:** puede crear sus propios alquileres (su ID se rellena automáticamente), sin necesidad de conocer IDs internos
- Al crear un alquiler, el vehículo se marca automáticamente como no disponible
- Al eliminar o finalizar un alquiler, el vehículo vuelve a estar disponible
- La tabla muestra nombres legibles en vez de IDs gracias a un JOIN en el DAO

### Usuarios
- Solo visible para empleados
- Permite editar nombre, apellidos, email y DNI de cualquier usuario

---

## Extensión: conversión de divisas

En el módulo de Vehículos hay un panel lateral que convierte el precio diario del vehículo seleccionado a otras monedas: USD, GBP, JPY, CHF y MXN.

**API utilizada:** [ExchangeRate-API](https://www.exchangerate-api.com/) (capa gratuita)  
**Integración:** `src/api/ExchangeRateService.java` hace una petición HTTP con `HttpURLConnection`, parsea la respuesta JSON manualmente y devuelve el tipo de cambio. La llamada se ejecuta en un hilo separado (`new Thread`) para no bloquear la interfaz gráfica mientras espera la respuesta. El resultado se actualiza en la UI con `SwingUtilities.invokeLater`.

---

## WakaTime

El tiempo de desarrollo ha sido registrado con WakaTime bajo el nombre de proyecto `Rentacar_LuciaMelero`.

https://wakatime.com/@d67ce14a-901e-439a-b990-c47ccd5ace0a/projects/ddewvrtzgs?start=2026-06-02&end=2026-06-08

---