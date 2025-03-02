# ARD Tarea 04 PMDM

## INTRODUCCIÓN

Esta aplicación muestra el funcionamiento de una guía interactiva en una app, esta app está inspirada en el mundo de **Spyro The Dragon**.

## CARACTERÍSTICAS PRINCIPALES

La guía interactiva es de obligado visionado. Hasta que no se haya completado, la app no dejará de darnos avisos hasta que la terminemos (es muy insistente).

### 1. Inicio de la Guía
Al ejecutar la app, nos aparecerá la pantalla de inicio de la guía, en la que se hace una breve descripción de la misma. Desde esta primera pantalla tenemos la opción de no ejecutar la guía, pero si queremos viajar por el mundo de **Spyro**, debemos pulsar el botón **Comenzar**.

### 2. Pestaña Personajes
Muestra los personajes que podemos encontrar en el mundo de **Spyro**.

### 3. Pestaña Mundos
Muestra los mundos disponibles.

### 4. Pestaña Colecciones
Muestra una lista con todos los objetos que podemos descubrir en el mundo de **Spyro**.

### 5. Easter Egg
Esta app esconde algunas sorpresas, ¿serás capaz de descubrirlas? ¡Adéntrate en el mundo de **Spyro** e intenta destapar lo que esconde!

## TECNOLOGÍAS UTILIZADAS

- **RecyclerView**: RecyclerView es un componente de la biblioteca **Android Jetpack** que permite mostrar listas o grids de datos de manera eficiente. Optimiza el rendimiento al reciclar las vistas de los elementos que salen de la pantalla, en lugar de crearlas repetidamente.
- **PreferencesHelper**: Es una utilidad (normalmente basada en **SharedPreferences** en Android) que se utiliza para guardar y recuperar datos persistentes simples, como configuraciones de usuario, preferencias o valores pequeños, en formato clave-valor.
- **AlertDialog**: Es un cuadro de diálogo emergente que muestra mensajes, opciones o botones de confirmación al usuario. Se usa para pedir confirmaciones o mostrar información importante.
- **SoundPool**: Es una clase que permite reproducir múltiples efectos de sonido de corta duración de manera eficiente. Ideal para sonidos en juegos o efectos rápidos (clics, explosiones, etc.).
- **MediaPlayer**: Se utiliza para reproducir archivos de audio o vídeo. Es más adecuado para contenido de mayor duración (como canciones o videos), a diferencia de `SoundPool`, que es para efectos cortos.
- **FrameLayout**: Es un contenedor de vistas en Android que permite apilar elementos uno encima del otro. Es útil cuando se quiere superponer vistas, como botones sobre imágenes.

## INSTRUCCIONES DE USO

Para poder disfrutar de esta aplicación, tan solo debes descargarla del repositorio oficial:

1. **Abrir Android Studio**: Ve a `File > New > Project from Version Control`.
2. **Pega la URL**: En el campo de URL, pega la dirección del repositorio de GitHub:
   
   ```
   https://github.com/TonyAlpha777/SpyroARD.git
   ```
3. **Selecciona la ubicación del proyecto**: Define la carpeta donde se descargará el proyecto.
4. **Haz clic en "Clonar"**: Android Studio descargará el repositorio y lo abrirá como un proyecto.

¡Listo para trabajar en el código! 🚀

## IMPORTANTE ⚠️

En la clase `CharactersFragment`, dentro del código en la estructura de control implementada en el `onViewCreated()`, hay una parte comentada con una breve instrucción. 

Si esta parte se deja tal cual, al completar la guía interactiva, cuando se vuelva a ejecutar la app, **ésta no aparecerá más**, ya que no contiene un apartado que permita volver a ejecutarla. 

En caso de querer hacer la guía de nuevo, sigue estos pasos:

1. **Descomentar el método `showConfirmationDialog()`**.
2. **Ejecutar `Run app`**.
3. **Aceptar el cuadro de diálogo** que aparece para que la guía se reinicie.
4. **Cerrar la app**.
5. **Comentar nuevamente el método `showConfirmationDialog()`**.
6. **Volver a ejecutar `Run app`**, así comenzamos de cero.

## CONCLUSIONES DEL DESARROLLADOR

El desarrollo de esta aplicación, aparentemente, parecía una tarea no muy complicada, hasta que me topé con los típicos problemas. Mi mayor dificultad fue al manejar las **preferencias de la app**. Creía que tenía claro su funcionamiento, pero nada más lejos de la realidad.

No había comprendido que las **preferencias se guardan en el terminal** (o en el emulador en este caso), lo que causó problemas en las estructuras de control. Cuando creía que una preferencia tenía el valor `false`, en realidad estaba guardada como `true`, lo que cambiaba totalmente la ejecución. Esto me causó grandes dolores de cabeza, ya que asumí erróneamente que los valores de **SharedPreferences** se reiniciaban al reinstalar la app, lo cual **no era así**.

De todo se aprende. También he aprendido a usar mejor el **LogCat**, lo que me ha permitido depurar errores de forma más efectiva.

Espero que mi app cumpla las expectativas. 😃

