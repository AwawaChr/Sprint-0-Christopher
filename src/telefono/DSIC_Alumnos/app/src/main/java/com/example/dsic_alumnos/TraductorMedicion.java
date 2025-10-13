package com.example.dsic_alumnos;

/**
 * TraductorMedicion:
 * Convierte una trama iBeacon en un objeto de medición
 * listo para enviarlo a la base de datos.
 *
 * Estructura usada en la trama iBeacon:
 *   - Major (2 bytes): [ byte alto = sensorId | byte bajo = contador ]
 *   - Minor (2 bytes): valor de la medición
 *
 * Cada placa se identifica por su MAC → no se guarda UUID ni RSSI.
 */
public class TraductorMedicion {

    // ----------------- Identificadores de sensores -----------------
    public static final int SENSOR_CO2   = 11;
    public static final int SENSOR_TEMP  = 12;
    public static final int SENSOR_RUIDO = 13;

    // ----------------- Campos del objeto medición ------------------
    private final String mac;     // MAC del dispositivo → identifica la placa
    private final int sensorId;   // tipo de sensor (CO2, TEMP, RUIDO, etc.)
    private final int contador;   // secuencia de lectura (byte bajo del major)
    private final int valor;      // valor numérico de la medición
    private final long timestamp; // momento de recepción (ms desde epoch)

    // ----------------- Constructor -----------------
    /**
     * Recibe:
     *   - mac: dirección MAC del dispositivo
     *   - tib: trama iBeacon con los datos de la medición
     */
    public TraductorMedicion(String mac, TramaIBeacon tib) {
        this.mac = mac;

        int major = Utilidades.bytesToInt(tib.getMajor());
        this.sensorId = (major >> 8) & 0xFF;   // byte alto → tipo de sensor
        this.contador = major & 0xFF;          // byte bajo → secuencia
        this.valor    = Utilidades.bytesToInt(tib.getMinor()); // valor medido

        this.timestamp = System.currentTimeMillis();
    }

    // ----------------- Getters -----------------
    public String getMac() { return mac; }
    public int getSensorId() { return sensorId; }
    public int getContador() { return contador; }
    public int getValor() { return valor; }
    public long getTimestamp() { return timestamp; }

    // ----------------- Descripción legible -----------------
    public String descripcion() {
        switch (sensorId) {
            case SENSOR_CO2:
                return "MAC=" + mac + " | CO2 = " + valor + " ppm (c=" + contador + ")";
            case SENSOR_TEMP:
                return "MAC=" + mac + " | Temperatura = " + valor + " °C (c=" + contador + ")";
            case SENSOR_RUIDO:
                return "MAC=" + mac + " | Ruido = " + valor + " dB (c=" + contador + ")";
            default:
                return "MAC=" + mac + " | Sensor " + sensorId + " → valor=" + valor + " (c=" + contador + ")";
        }
    }
}
