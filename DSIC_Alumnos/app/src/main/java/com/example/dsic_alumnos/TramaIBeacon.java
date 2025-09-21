package com.example.dsic_alumnos;

import java.util.Arrays;

/**
 * Clase que representa una trama iBeacon a partir de un array de bytes.
 * Sirve para "trocear" la trama en sus distintas secciones y acceder a ellas.
 *
 * Un iBeacon envía su información en una trama con esta estructura:
 *
 * [ Prefijo (9 bytes) | UUID (16 bytes) | Major (2 bytes) | Minor (2 bytes) | TxPower (1 byte) ]
 *
 * El prefijo a su vez contiene:
 *   [ AdvFlags (3) | AdvHeader (2) | CompanyID (2) | iBeaconType (1) | iBeaconLength (1) ]
 *
 * Total: 30 bytes
 */
public class TramaIBeacon {

    // ------------------ CAMPOS PRINCIPALES DE LA TRAMA ------------------

    private byte[] prefijo = null; // 9 bytes → cabecera general de la trama
    private byte[] uuid = null;    // 16 bytes → identificador único del beacon
    private byte[] major = null;   // 2 bytes  → agrupación (ej: zona de un edificio)
    private byte[] minor = null;   // 2 bytes  → identificador más específico (ej: beacon dentro de la zona)
    private byte txPower = 0;      // 1 byte   → potencia de transmisión medida a 1 metro (usada para calcular distancia)

    // Copia completa de los bytes originales de la trama
    private byte[] losBytes;

    // ------------------ CAMPOS DETALLADOS DEL PREFIJO ------------------

    private byte[] advFlags = null;   // 3 bytes → indican capacidades del anuncio (ej: Bluetooth LE)
    private byte[] advHeader = null;  // 2 bytes → tipo de paquete publicitario
    private byte[] companyID = new byte[2]; // 2 bytes → identificador del fabricante (Apple: 0x004C)
    private byte iBeaconType = 0;     // 1 byte  → tipo de beacon (0x02 = iBeacon)
    private byte iBeaconLength = 0;   // 1 byte  → longitud del payload de iBeacon

    // ------------------ MÉTODOS GETTER ------------------
    // Devuelven cada parte de la trama

    public byte[] getPrefijo() { return prefijo; }
    public byte[] getUUID() { return uuid; }
    public byte[] getMajor() { return major; }
    public byte[] getMinor() { return minor; }
    public byte getTxPower() { return txPower; }
    public byte[] getLosBytes() { return losBytes; }
    public byte[] getAdvFlags() { return advFlags; }
    public byte[] getAdvHeader() { return advHeader; }
    public byte[] getCompanyID() { return companyID; }
    public byte getiBeaconType() { return iBeaconType; }
    public byte getiBeaconLength() { return iBeaconLength; }

    // ------------------ CONSTRUCTOR ------------------
    /**
     * Recibe la trama completa (30 bytes) y separa cada campo.
     */
    public TramaIBeacon(byte[] bytes) {
        this.losBytes = bytes;

        // Extraer campos principales de la trama
        prefijo = Arrays.copyOfRange(losBytes, 0, 9);   // [0..8] → 9 bytes
        uuid = Arrays.copyOfRange(losBytes, 9, 25);     // [9..24] → 16 bytes
        major = Arrays.copyOfRange(losBytes, 25, 27);   // [25..26] → 2 bytes
        minor = Arrays.copyOfRange(losBytes, 27, 29);   // [27..28] → 2 bytes
        txPower = losBytes[29];                         // [29] → 1 byte

        // Extraer subcampos del prefijo
        advFlags = Arrays.copyOfRange(prefijo, 0, 3);    // [0..2] → 3 bytes
        advHeader = Arrays.copyOfRange(prefijo, 3, 5);   // [3..4] → 2 bytes
        companyID = Arrays.copyOfRange(prefijo, 5, 7);   // [5..6] → 2 bytes
        iBeaconType = prefijo[7];                        // [7] → 1 byte
        iBeaconLength = prefijo[8];                      // [8] → 1 byte
    }
}
