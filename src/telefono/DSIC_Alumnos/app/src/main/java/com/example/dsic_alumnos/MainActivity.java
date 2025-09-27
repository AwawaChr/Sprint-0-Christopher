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

    // ------------------------- INICIALIZACIÓN -------------------------
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos adaptador BT");
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): habilitamos adaptador BT");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        bta.enable();
        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): habilitado = " + bta.isEnabled());
        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): estado = " + bta.getState());

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos escaner btle");
        this.elEscanner = bta.getBluetoothLeScanner();

        if (this.elEscanner == null) {
            Log.d(ETIQUETA_LOG, "Socorro: NO hemos obtenido escaner btle !!!!");
        }

        // Pedir permisos si no los tiene
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PETICION_PERMISOS);
        } else {
            Log.d(ETIQUETA_LOG, "Ya tenemos los permisos necesarios.");
        }
    }

    // ------------------------- BÚSQUEDA DE TODOS LOS BEACONS -------------------------
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

    // ------------------------- MOSTRAR INFORMACIÓN -------------------------
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {
        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        // ----------------- FILTRO DE NOMBRE -----------------
        String nombreDispositivo = "<sin nombre>";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (bluetoothDevice.getName() != null) {
                nombreDispositivo = bluetoothDevice.getName();
            } else {
                // Ignorar dispositivos sin nombre
                return;
            }
        }

        Log.d(ETIQUETA_LOG, "****************************************************");
        Log.d(ETIQUETA_LOG, "nombre = " + nombreDispositivo);
        Log.d(ETIQUETA_LOG, "dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, "rssi = " + rssi);
        Log.d(ETIQUETA_LOG, "bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, "bytes (hex) = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);

        Log.d(ETIQUETA_LOG, "prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "iBeacon length = " + Integer.toHexString(tib.getiBeaconLength()));
        Log.d(ETIQUETA_LOG, "uuid = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, "major = " + Utilidades.bytesToInt(tib.getMajor()));
        Log.d(ETIQUETA_LOG, "minor = " + Utilidades.bytesToInt(tib.getMinor()));
        Log.d(ETIQUETA_LOG, "txPower = " + tib.getTxPower());
        Log.d(ETIQUETA_LOG, "****************************************************");
    }

    // ------------------------- BOTONES -------------------------
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "boton buscar dispositivos BTLE Pulsado");
        buscarTodosLosDispositivosBTLE();
    }

    // ------------------------- CICLO DE VIDA -------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(ETIQUETA_LOG, "onCreate(): empieza");
        inicializarBlueTooth();
        Log.d(ETIQUETA_LOG, "onCreate(): termina");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PETICION_PERMISOS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(ETIQUETA_LOG, "permisos concedidos");
            } else {
                Log.d(ETIQUETA_LOG, "Socorro: permisos NO concedidos");
            }
        }
    }
}
