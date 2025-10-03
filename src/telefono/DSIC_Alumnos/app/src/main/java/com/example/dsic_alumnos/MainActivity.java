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
    private ScanCallback callbackDelEscaneo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(ETIQUETA_LOG, "onCreate(): empieza");

        inicializarBlueTooth();

        Log.d(ETIQUETA_LOG, "onCreate(): termina");
    }

    private void inicializarBlueTooth() {
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(ETIQUETA_LOG, "Sin permiso BLUETOOTH_CONNECT, saliendo");
            return;
        }
        bta.enable();
        this.elEscanner = bta.getBluetoothLeScanner();

        // Pedir permisos si no los tenemos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PETICION_PERMISOS
            );
        } else {
            Log.d(ETIQUETA_LOG, "Permisos ya concedidos");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PETICION_PERMISOS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(ETIQUETA_LOG, "Permisos concedidos");
            } else {
                Log.d(ETIQUETA_LOG, "Permisos NO concedidos");
            }
        }
    }

    // ------------------ BOTÓN PARA BUSCAR TODOS LOS DISPOSITIVOS ------------------
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "Botón buscar dispositivos BTLE pulsado");
        buscarTodosLosDispositivosBTLE();
    }

    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): empieza");

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
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        elEscanner.startScan(callbackDelEscaneo);
    }

    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {
        BluetoothDevice device = resultado.getDevice();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Filtrar solo dispositivos llamados "GTI"
        String nombre = device.getName();
        if (nombre == null || !nombre.equals("GTI")) {
            return; // Ignorar dispositivos sin nombre o con otro nombre
        }

        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        TramaIBeacon tib = new TramaIBeacon(bytes);

        // Crear objeto con datos procesados
        BeaconData bd = new BeaconData();
        bd.setMajor(Utilidades.bytesToInt(tib.getMajor()));
        bd.setMinor(Utilidades.bytesToInt(tib.getMinor()));
        bd.setTxPower(tib.getTxPower());
        bd.setTimestamp(System.currentTimeMillis());
        bd.setMac(device.getAddress()); // Cada MAC es una placa

        Log.d(ETIQUETA_LOG, "Beacon procesado: MAC=" + bd.getMac()
                + " Major=" + bd.getMajor()
                + " Minor=" + bd.getMinor()
                + " TxPower=" + bd.getTxPower());
    }

    // ------------------ BOTÓN PARA DETENER LA BÚSQUEDA ------------------
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        if (callbackDelEscaneo == null) return;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        elEscanner.stopScan(callbackDelEscaneo);
        callbackDelEscaneo = null;
    }

    // ------------------ BOTÓN OPCIONAL: BUSCAR UN DISPOSITIVO ESPECÍFICO ------------------
    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        // Ejemplo: solo buscar el dispositivo llamado "GTI"
        // Actualmente todo ya filtra por nombre "GTI" en mostrarInformacionDispositivoBTLE()
    }
}
