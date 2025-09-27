package com.example.dsic_alumnos;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ETIQUETA_LOG = ">>>>";
    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    private BluetoothLeScanner elEscanner;
    private ScanCallback callbackDelEscaneo;

    // ------------------ Inicializar Bluetooth y escáner ------------------
    private void inicializarBlueTooth() {
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        if (bta == null) {
            Log.d(ETIQUETA_LOG, "Bluetooth no soportado en este dispositivo");
            return;
        }

        // Solicitar permisos en tiempo de ejecución
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PETICION_PERMISOS);
            return;
        }

        if (!bta.isEnabled()) {
            bta.enable();
        }

        elEscanner = bta.getBluetoothLeScanner();
        if (elEscanner == null) {
            Log.d(ETIQUETA_LOG, "No se pudo obtener el escáner BLE. Asegúrate de que Bluetooth esté activo.");
        } else {
            Log.d(ETIQUETA_LOG, "Escáner BLE inicializado correctamente.");
        }
    }

    // ------------------ Escaneo de todos los dispositivos ------------------
    private void buscarTodosLosDispositivosBTLE() {
        if (elEscanner == null) {
            Log.d(ETIQUETA_LOG, "elEscanner es null, no puedo escanear");
            return;
        }

        callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "Scan failed: " + errorCode);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        elEscanner.startScan(callbackDelEscaneo);
        Log.d(ETIQUETA_LOG, "Escaneo iniciado.");
    }

    // ------------------ Mostrar info de dispositivos ------------------
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {
        BluetoothDevice bluetoothDevice = resultado.getDevice();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String nombre = bluetoothDevice.getName();
        if (nombre == null) return; // ignorar dispositivos sin nombre

        int rssi = resultado.getRssi();
        Log.d(ETIQUETA_LOG, "Dispositivo detectado: nombre=" + nombre + " dirección=" + bluetoothDevice.getAddress() + " RSSI=" + rssi);
    }

    // ------------------ Botón Buscar ------------------
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "Botón buscar dispositivos BTLE pulsado");
        buscarTodosLosDispositivosBTLE();
    }

    // ------------------ Detener escaneo ------------------
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        if (elEscanner != null && callbackDelEscaneo != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            elEscanner.stopScan(callbackDelEscaneo);
            callbackDelEscaneo = null;
            Log.d(ETIQUETA_LOG, "Escaneo detenido.");
        }
    }

    // ------------------ onCreate ------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializarBlueTooth();
    }

    // ------------------ Resultado de permisos ------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PETICION_PERMISOS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(ETIQUETA_LOG, "Permisos concedidos");
                inicializarBlueTooth();
            } else {
                Log.d(ETIQUETA_LOG, "Permisos NO concedidos");
            }
        }
    }
}
