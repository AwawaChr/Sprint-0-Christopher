Proyecto Biometría – Sprint 0 (2025)

Este repositorio forma parte del proyecto de Biometría Ambiental 2025, enfocado en la captura, envío y visualización de mediciones ambientales mediante dispositivos BLE iBeacon, una aplicación Android como receptor, y un servidor REST que centraliza las mediciones en una base de datos MySQL.

Estructura del proyecto
Arduino / C++ (Emisor BLE)

Encargado de generar tramas iBeacon y simular mediciones ambientales (CO₂, temperatura, ruido).

Archivos principales:

HolaMundoBeacon.ino – Sketch principal del emisor.

Publicador.h – Publicación periódica de valores en BLE.

EmisoraBLE.h – Configuración y emisión de tramas iBeacon.

ServicioEnEmisora.h – Definición de servicios BLE personalizados.

Medidor.h – Generación de lecturas simuladas.

PuertoSerie.h – Comunicación serial para depuración y logs.

Flujo de datos:

El emisor lee valores simulados de sensores.

Los publica en paquetes iBeacon con major, minor y RSSI.

Se pueden emitir anuncios personalizados con emitirAnuncioIBeaconLibre.

Android / Java (Receptor BLE)

Aplicación móvil para escaneo de beacons, decodificación de tramas y envío de datos al servidor.

Archivos destacados:

MainActivity.java – Control principal del escaneo BLE y la UI.

APIHelper.java – Encapsula la comunicación con el servidor REST.

TramaIBeacon.java – Decodificación de los paquetes iBeacon.

Utilidades.java – Funciones auxiliares para procesamiento de datos.

Flujo de datos:

Escanea dispositivos BLE cercanos.

Filtra por nombre de dispositivo o muestra todos los detectados.

Extrae MAC, major, minor, txPower y timestamp.

Envía las mediciones al servidor mediante POST /insertar.

Servidor REST (Node.js + Express)

Backend encargado de recibir, validar y almacenar las mediciones en MySQL, así como ofrecer endpoints para consulta.

Endpoints principales:

GET /mediciones → Devuelve todas las mediciones en formato JSON.

POST /insertar → Inserta nuevas mediciones recibidas desde Android.

Carpeta principal del servidor: src/servidor

Dependencias: express, body-parser, cors, mysql2.

Flujo de datos:

Recibe mediciones desde la app Android.

Valida los campos (mac, major, minor, txPower, timestamp).

Inserta los datos en la tabla mediciones de MySQL.

Permite consultas en tiempo real desde el cliente web.

Cliente Web

Interfaz ligera para visualización de mediciones almacenadas.

Características:

Consulta el servidor REST con GET /mediciones.

Renderiza las mediciones en una tabla dinámica:

ID, MAC, Major, Minor, TxPower, Timestamp.

Permite monitoreo en tiempo real desde cualquier navegador en la red local.

Tecnologías utilizadas

C++ / Arduino IDE – Emisión BLE y simulación de sensores.

Adafruit Feather nRF52840 + Bluefruit Library – Hardware BLE.

Java / Android Studio – Escaneo y envío de tramas BLE.

Node.js + Express – Backend REST.

MySQL – Persistencia de datos.

HTML / JS / CSS – Visualización web.

Git / GitHub Desktop – Control de versiones.

Cómo ejecutar el proyecto
1. Emisor (Arduino)

Abrir HolaMundoBeacon.ino en Arduino IDE.

Seleccionar la placa Adafruit Feather nRF52840 Express.

Subir el sketch al dispositivo.

El beacon comenzará a emitir datos simulados de CO₂, temperatura y otros parámetros.

2. Receptor (Android)

Abrir proyecto DSIC_Alumnos en Android Studio.

Conceder permisos de Bluetooth y ubicación.

Ejecutar la app en un dispositivo Android.

Los datos detectados se envían automáticamente al servidor REST.

3. Servidor (Node.js)

Abrir terminal en la carpeta src/servidor.

Instalar dependencias:

npm install


Iniciar el servidor:

node mainServidorREST.js


Comprobar funcionamiento en navegador:

http://localhost:8080/


Debe mostrar: Servidor REST escuchando.

4. Cliente Web

Abrir index.html en navegador.

Ver la tabla de mediciones y comprobar que se actualiza automáticamente.

Flujo de datos completo
Arduino (Publicador BLE)
  └─> iBeacon (MAC, Major, Minor, RSSI)
       └─> Android (DSIC_Alumnos)
            └─> POST /insertar
                 └─> Servidor REST (Node.js + MySQL)
                      └─> GET /mediciones
                           └─> Cliente Web (tabla dinámica)


Arduino: Genera y emite datos de sensores en paquetes BLE.

Android: Detecta, decodifica y envía mediciones al backend.

Servidor REST: Valida y almacena las mediciones.

Cliente Web: Visualiza los datos en tiempo real.