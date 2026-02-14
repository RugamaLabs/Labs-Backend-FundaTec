# Documentación de Refactorización a lo Casual

¡Hola! Aquí te explico qué onda con los cambios que hicimos para limpiar el código de la vista de tareas. La idea fue no repetir el mismo bloque de HTML tres veces (uno para pendientes, otro para en progreso, otro para terminadas). ¡Qué pereza mantener eso!

Así que usamos **Thymeleaf** para hacerlo más pro. Aquí los trucos que usamos:

## 1. Fragmentos (`th:fragment`)
Imagínate que un fragmento es como un "snippet" o un pedazo de código que guardas para usar después.
En el archivo `fragments/fragment.html`, definimos un bloque así:
```html
<div th:fragment="taskList(title, tasks, itemClass, showDeadline)">
```
Esto le dice a Thymeleaf: "Hey, este pedazo de HTML se llama `taskList` y acepta variables como `title` (el título de la sección), `tasks` (la lista de tareas), etc.". Es como definir una función en programación pero para HTML.

## 2. Reemplazos (`th:replace`)
En el archivo principal `usertasks.html`, en lugar de escribir todo el código de nuevo, simplemente llamamos a nuestro fragmento.
```html
<div th:replace="~{fragments/fragment :: taskList('Pendientes', ${pendingTasks}, 'pending', true)}"></div>
```
`th:replace` le dice a Thymeleaf: "Borra este `<div>` y pon aquí el contenido del fragmento `taskList` que está en el archivo `fragments/fragment.html`". El `~{...}` es la sintaxis mágica para referenciarlo.

## 3. Pasar Variables (Parámetros)
Fíjate que al llamar al fragmento le pasamos cosas:
- `'Pendientes'`: Un texto simple para el título.
- `${user.tasks.?[status.name() == 'PENDING']}`: Una expresión loca de Spring (SpEL) para filtrar solo las tareas pendientes.
- `'pending'`: Una clase CSS para que se vea del color correcto.
- `true`: Un booleano para decirle "sí, muestra la fecha límite en esta lista".

## 4. Iteración y Condicionales (`th:each`, `th:if`)
Dentro del fragmento, seguimos usando lo clásico:
- `th:if="${#lists.isEmpty(tasks)}"`: Si la lista está vacía, mostramos un mensaje tipo "No hay tareas...".
- `th:each="task : ${tasks}"`: Recorremos la lista que nos pasaron y pintamos cada tarea.

¡Y listo! Con esto, si quieres cambiar cómo se ve una tarjeta de tarea, solo cambias `fragments/fragment.html` y se actualiza en las tres columnas. ¡Magia pura! ✨
