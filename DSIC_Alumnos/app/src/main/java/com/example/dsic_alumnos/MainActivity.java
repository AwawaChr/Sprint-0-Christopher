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

    // ------------------ ONCREATE ------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ETIQUETA_LOG, "onCreate(): empieza");
        inicializarBlueTooth();
        Log.d(ETIQUETA_LOG, "onCreate(): termina");
    }

    // ------------------ INICIALIZAR BLUETOOTH ------------------
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos adaptador BT");
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        if (bta == null) {
            Log.d(ETIQUETA_LOG, "No hay adaptador Bluetooth disponible");
            return;
        }

        // Habilitar Bluetooth si no está activo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return; // Salir si no hay permisos
        }
        bta.enable();
        this.elEscanner = bta.getBluetoothLeScanner();

        // Pedir permisos si no los tenemos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PETICION_PERMISOS);
        }
    }

    // ------------------ ESCANEAR TODOS LOS BEACONS ------------------
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): empieza");

        if (elEscanner == null) {
            Log.d(ETIQUETA_LOG, "elEscanner es null, no puedo escanear");
            return;
        }

        this.callbackDelEscaneo = new ScanCallback() {
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

    // ------------------ DETENER ESCANEO ------------------
    private void detenerBusquedaDispositivosBTLE() {
        if (callbackDelEscaneo == null || elEscanner == null) return;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        elEscanner.stopScan(callbackDelEscaneo);
        callbackDelEscaneo = null;
    }

    // ------------------ MOSTRAR INFO DEL BEACON ------------------
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        TramaIBeacon tib = new TramaIBeacon(bytes);

        // Crear objeto con datos procesados
        BeaconData bd = new BeaconData();
        bd.uuid = Utilidades.bytesToHexString(tib.getUUID());
        bd.major = Utilidades.bytesToInt(tib.getMajor());
        bd.minor = Utilidades.bytesToInt(tib.getMinor());
        bd.txPower = tib.getTxPower();
        bd.rssi = rssi;
        bd.timestamp = System.currentTimeMillis();

        Log.d(ETIQUETA_LOG, "Beacon detectado: UUID=" + bd.uuid
                + " Major=" + bd.major
                + " Minor=" + bd.minor
                + " RSSI=" + bd.rssi
                + " TxPower=" + bd.txPower);

        // Luego insertar en SQLite o mandar al backend
        // insertarEnBaseDeDatos(bd);
        // enviarAlBackend(bd);
    }

    // ------------------ BOTONES ------------------
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        buscarTodosLosDispositivosBTLE();
    }

    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        detenerBusquedaDispositivosBTLE();
    }

    // ------------------ CALLBACK PERMISOS ------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CODIGO_PETICION_PERMISOS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(ETIQUETA_LOG, "Permisos concedidos");
            } else {
                Log.d(ETIQUETA_LOG, "Permisos NO concedidos");
            }
        }
    }
}
