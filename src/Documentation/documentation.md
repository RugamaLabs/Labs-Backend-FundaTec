# Documentación de Cambios: Transición de Estado de Tareas

A continuación se detalla cómo se implementó la funcionalidad para permitir a los usuarios mover las tareas por los diferentes estados: **PENDING (Pendientes)**, **INPROGRESS (En Progreso)** y **DONE (Completadas)**.

## Motivación

Inicialmente, las tareas se creaban en estado "Pendiente" sin posibilidad de actualizarlas. Se requería que en cada tarjeta de tarea existiera un botón para moverla a la siguiente etapa, de modo que el flujo fuera:

1. **Pendiente** → (al dar click en "Iniciar") → **En Progreso**
2. **En Progreso** → (al dar click en "Completar") → **Completadas**
3. Las tareas **Completadas** no requieren más transiciones.

## Cambios Realizados y Justificación

A continuación detallamos el flujo de datos ordenado en el que intervienen los componentes, respetando el ciclo de vida de la petición:

### 1. Vista (Frontend / Thymeleaf)
- **Archivo modificado**: `src/main/resources/templates/usertasks.html`
- **Explicación**: Se agregó un formulario (`<form>`) dentro de las tarjetas correspondientes a tareas `PENDING` y tareas `INPROGRESS`. Estos formularios apuntan a la ruta de avance de estado usando el ID de la tarea (`/user/tasks/{id}/advance`) por medio de un método POST. 
- **Justificación**: *¿Por qué un formulario POST y no un simple enlace HTTP normal?* Porque un cambio de estado es una operación destructiva/alteradora en el sistema (modifica datos). Los estándares web y de seguridad nos indican que las operaciones de modificación deben emplear métodos seguros como un envío `POST` y jamás un `GET` (como lo haría un hipervínculo `<a>`). Esto evita, por ejemplo, que al refrescar por pantalla alguien mueva accidentalmente una tarea simplemente recargando la página.  

### 2. Controlador (Controller)
- **Archivo modificado**: `src/main/java/teccr/justdoitcloud/controller/UserTasksController.java`
- **Explicación**: Se añadió el método `@PostMapping("/{taskId}/advance")` que se dispara cuando la persona aprieta el botón de "Iniciar" o "Completar". Este método extrae el identificador de la tarea, le pasa la batuta al **Servicio**, y de último hace un redireccionamiento a la misma pantalla de tareas.
- **Justificación**: Mantuvimos el código del controlador lo más delgado y ligero posible. Extraer el ID de la URL (`@PathVariable`) lo hace súper transparente (se sabe exactamente qué recurso se afecta simplemente viendo la flecha de la petición web). El redireccionamiento (`redirect:/user/tasks`) obedece al patrón "Post-Redirect-Get" (PRG), usado para prevenir escenarios molestos donde el usuario oprime el botón de 'Atrás' en el navegador y re-envía por error la solicitud nuevamente.

### 3. Entidad (Entity)
- **Archivo modificado**: `src/main/java/teccr/justdoitcloud/data/Task.java`
- **Explicación**: Se quitó la palabra reservada `final` que acompañaba a la variable `status`. Esto hace que Lombok pueda generar y ofrecer un método modificado u "actualizador" implícito (`setStatus`).
- **Justificación**: En la filosofía de la programación Java, cuando marcas una variable como `final`, esta no puede cambiar de valor bajo ninguna circunstancia a lo largo de su existencia. Dada nuestra nueva regla de negocio en donde un ítem efectivamente avanza (cambia de estado temporal), era lógicamente incompatible seguir catalogando al estado como inamovible, por ende fue removido.

### 4. Servicio (Service)
- **Archivo modificado**: `src/main/java/teccr/justdoitcloud/service/TaskService.java`
- **Explicación**: Es el "cerebro" o la "lógica de negocio". Se añadió el método `advanceTaskStatus(Long taskId, User user)`. Este se encarga de:
  1. Extraer a quién le pertenece la tarea.
  2. Subirla de posición solo si las reglas lo permiten.
  3. Indicar que el nuevo estatus se guarde.
- **Justificación**: Toda decisión vital debe residir acá en esta capa central. Principalmente tomamos la decisión de hacer una verificación rigurosa de pertenencia: _"¿El usuario logueado en la sesión es el verdadero autor o dueño de la tarea X que viene del botón?"_. Es una medida altamente esencial de ciberseguridad para mitigar que perfiles traviesos en internet traten de manipular y avanzar las tareas de otros usuarios utilizando mañas como enviar un ID ajeno.

### 5. Repositorio (Repository)
- **Archivo involucrado**: `src/main/java/teccr/justdoitcloud/repository/TaskRepository.java` 
- **Explicación y Justificación**: Sirve de puente conversacional con la base de datos SQL. Al mirar el código de nuestro `TaskRepository`, te darás cuenta de algo curioso: **¡no hay ningún método llamado `save()` ni de actualización escrito por nosotros!** ¿Por qué no lo añadimos manualmente?

  La razón es que Spring Boot, el "motor principal" de nuestra aplicación, viene equipado con una poderosa herramienta llamada `CrudRepository`. Al hacer que nuestro repositorio simplemente herede de esta herramienta (usando `extends CrudRepository`), Spring entra en acción y, tras bambalinas, nos regala un paquete completo de métodos mágicos, listos para usar, incluyendo `save()`, `findById()`, entre otros.

  **¿Cómo funciona exactamente esta magia sin estar escrita en el código?**
  A pesar de que no escribimos el método `save()`, cuando nuestro *Servicio* lo invoca, Spring Data toma las riendas y analiza de forma súper inteligente el objeto que le pasamos:
  1. Primero, revisa si nuestra tarea ya tiene un número o clave única (`id`), lo cual es cierto, ya que la trajimos desde la base de datos unos segundos antes usando `findById()`.
  2. Al detectar que ya posee un `id`, se da cuenta instantáneamente de que **no es una tarea nueva**, sino que es una vieja conocida.
  3. En lugar de utilizar una consulta genérica de agregar registro (`INSERT`), detecta que el estado difiere y procede a ejecutar secretamente en la base de datos un comando de actualización específico (`UPDATE TAREAS SET STATUS = ...`).
  
  Decidimos apoyarnos totalmente en esta funcionalidad estándar en lugar de inventar la rueda diseñando consultas a mano. De esta manera, ganamos dos cosas invaluables: mantenemos nuestro archivo visualmente limpio y dejamos que el sistema gestione la actualización de forma sumamente optimizada, automática y segura.

## Conclusión

Con estos cambios hemos establecido un flujo de actualización (Update) con total seguridad y coherencia. La vista reacciona gracias a Thymeleaf que enlista los nuevos resultados filtrados por el estado, los controladores distribuyen bien las peticiones de avance, los servicios realizan la parte pensante (lógica de avance y validaciones de datos), y el repositorio cumple su labor delegando el pesado manejo lógico a Spring, sin requerir recargas innecesarias.
