# Consumo de API RESTful (Supabase) en Android con Volley

Aplicación Android nativa (Java) que consume la API REST de Supabase mediante **Volley**,
procesa la respuesta JSON con `JSONArray` y `JSONObject` y muestra la lista de alumnos en un `TextView`.

**Autor:** Justyn Keith Cruz Perez  
**Materia:** Aplicaciones Móviles - UTEQ (Ingeniería de Software, 6to nivel)

## Funcionamiento
1. Al iniciar, `MainActivity` envía una solicitud HTTP GET a `/rest/v1/alumnos` de Supabase con Volley (`JsonArrayRequest`).
2. La respuesta es un arreglo JSON; se recorre con `JSONArray`/`JSONObject` y se arma un texto con nombre, paralelo, periodo y correo.
3. El texto se muestra en un `TextView` dentro de un `ScrollView`.
4. Se manejan: sin conexión, timeout, errores HTTP 401/403/404, respuesta vacía y errores de parseo JSON.

## Configuración (las claves NO están en el repositorio)
1. Crea un proyecto en [supabase.com](https://supabase.com) y la tabla `alumnos`.
2. Activa RLS y crea una política de solo lectura (`select`).
3. Copia `local.properties.example` como `local.properties` (en la raíz) y completa:
   ```
   supabase.url=https://TU-PROYECTO.supabase.co
   supabase.key=TU_CLAVE_PUBLISHABLE_O_ANON
   ```
   `local.properties` está en `.gitignore`. Solo se usa la clave publishable/anon, limitada por RLS.
4. Abre el proyecto en Android Studio y ejecútalo en un emulador o dispositivo.

## Capturas
Tabla `alumnos` en Supabase:

![Tabla alumnos](docs/captura-supabase-tabla.png)

Aplicación en ejecución:

![App en el emulador](docs/captura-app-emulador.png)

## Tecnologías
Java · Android Studio · Volley 1.2.1 · Supabase (PostgreSQL + REST) · Gradle Kotlin DSL
