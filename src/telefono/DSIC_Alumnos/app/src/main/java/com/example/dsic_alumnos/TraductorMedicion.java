package com.example.dsic_alumnos;

/**
 * TraductorMedicion:
 * Clase auxiliar que convierte una trama iBeacon en una medición entendible
 * (sensorId, contador y valor). La idea es tener un objeto sencillo para
 * enviar/guardar en la base de datos.
 *
 * Estructura usada:
 *   - Major (2 bytes): [ byte alto = sensorId | byte bajo = contador ]
 *   - Minor (2 bytes): valor de la medición
 */
public class TraductorMedicion {

    // ----------------- Identificadores de sensores -----------------
    public static final int SENSOR_CO2   = 11;
    public static final int SENSOR_TEMP  = 12;
    public static final int SENSOR_RUIDO = 13;

    // ----------------- Campos del objeto medición ------------------
    private final int sensorId;   // tipo de sensor (CO2, TEMP, RUIDO, etc.)
    private final int contador;   // secuencia para diferenciar lecturas
    private final int valor;      // valor numérico de la medición

    // ----------------- Constructor -----------------
    /**
     * Recibe la trama iBeacon y extrae:
     *   - sensorId y contador desde "major"
     *   - valor desde "minor"
     */
    public TraductorMedicion(TramaIBeacon tib) {
        int major = Utilidades.bytesToInt(tib.getMajor());
        this.sensorId = (major >> 8) & 0xFF;  // byte alto → tipo de sensor
        this.contador = major & 0xFF;         // byte bajo → número de secuencia
        this.valor    = Utilidades.bytesToInt(tib.getMinor()); // valor medido
    }

    // ----------------- Getters -----------------
    public int getSensorId() { return sensorId; }
    public int getContador() { return contador; }
    public int getValor() { return valor; }

    // ----------------- Descripción legible -----------------
    /**
     * Devuelve una cadena descriptiva con el tipo de sensor,
     * el valor leído y el contador.
     */
    public String descripcion() {
        switch (sensorId) {
            case SENSOR_CO2:
                return "CO2 = " + valor + " ppm (c=" + contador + ")";
            case SENSOR_TEMP:
                return "Temperatura = " + valor + " °C (c=" + contador + ")";
            case SENSOR_RUIDO:
                return "Ruido = " + valor + " dB (c=" + contador + ")";
            default:
                return "Sensor " + sensorId + " → valor=" + valor + " (c=" + contador + ")";
        }
    }
}
