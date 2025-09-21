package com.example.dsic_alumnos;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Build;
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

    // Repositorio DB
    private BeaconRepository beaconRepo;

    // ---------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(ETIQUETA_LOG, " onCreate(): empieza ");

        // inicializa repositorio DB
        beaconRepo = new BeaconRepository(this);

        inicializarBlueTooth();
        Log.d(ETIQUETA_LOG, " onCreate(): termina ");
    }

    // ---------------------
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        if (bta == null) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): tu dispositivo NO tiene Bluetooth");
            return;
        }

        // Pedir permisos necesarios según versión
        if (!tengoPermisosBluetooth()) {
            pedirPermisosBluetooth();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Si no hay permiso, no seguimos (Android R+ requiere permiso para enable())
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): sin permiso BLUETOOTH_CONNECT, saliendo");
            return;
        }
        bta.enable();
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitado =  " + bta.isEnabled() );
        this.elEscanner = bta.getBluetoothLeScanner();
        if ( this.elEscanner == null ) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");
        }
    }

    // ---------------------
    private boolean tengoPermisosBluetooth() {
        // Comprobamos permisos relevantes (pedimos todos para simplificar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void pedirPermisosBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION },
                    CODIGO_PETICION_PERMISOS
            );
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    CODIGO_PETICION_PERMISOS
            );
        }
    }

    // ---------------------
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empieza ");

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
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanFailed() code=" + errorCode);
            }
        };

        if (this.elEscanner == null) {
            Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): elEscanner es null, no puedo escanear");
            return;
        }

        // Comprobación permisos
        if (!tengoPermisosBluetooth()) {
            Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): no tengo permisos, pido permisos");
            pedirPermisosBluetooth();
            return;
        }

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
        this.elEscanner.startScan(this.callbackDelEscaneo);
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): escaneo iniciado");
    }

    // ---------------------
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {
        if (resultado == null) return;

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = (resultado.getScanRecord() != null) ? resultado.getScanRecord().getBytes() : null;
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(ETIQUETA_LOG, " mostrarInformacionDispositivoBTLE(): sin permiso BLUETOOTH_CONNECT");
            return;
        }
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi );

        if (bytes == null) {
            Log.d(ETIQUETA_LOG, " mostrarInformacionDispositivoBTLE(): ScanRecord bytes == null");
            return;
        }
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        // Evitar errores si la trama es más corta de lo esperado
        if (bytes.length < 30) {
            Log.d(ETIQUETA_LOG, " mostrarInformacionDispositivoBTLE(): trama demasiado corta, length=" + bytes.length);
            return;
        }

        // Parseamos la trama iBeacon
        TramaIBeacon tib;
        try {
            tib = new TramaIBeacon(bytes);
        } catch (Exception e) {
            Log.d(ETIQUETA_LOG, " mostrarInformacionDispositivoBTLE(): error al parsear TramaIBeacon: " + e.getMessage());
            return;
        }

        // Extraer major (2 bytes) y convertir a int (esto será nuestro "codigo")
        int majorInt;
        try {
            majorInt = Utilidades.bytesToIntOK(tib.getMajor());
        } catch (Exception e) {
            Log.d(ETIQUETA_LOG, " mostrarInformacionDispositivoBTLE(): error al convertir major a int: " + e.getMessage());
            return;
        }
        String codigo = String.valueOf(majorInt);

        Log.d(ETIQUETA_LOG, " UUID(hex) = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major = " + Utilidades.bytesToHexString(tib.getMajor()) + " ( " + majorInt + " )");
        Log.d(ETIQUETA_LOG, " minor = " + Utilidades.bytesToHexString(tib.getMinor()) + " ( " + Utilidades.bytesToIntOK(tib.getMinor()) + " )");

        // Insertar en BD en hilo de fondo
        final String codigoFinal = codigo;
        new Thread(() -> {
            long idGuardado = beaconRepo.insertarCodigo(codigoFinal);
            Log.d(ETIQUETA_LOG, " Dato insertado con id=" + idGuardado + " codigo=" + codigoFinal);
        }).start();
    }

    // ---------------------
    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): buscando " + dispositivoBuscado);

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                mostrarInformacionDispositivoBTLE(resultado);
            }
        };

        if (this.elEscanner == null) {
            Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): elEscanner null");
            return;
        }

        if (!tengoPermisosBluetooth()) {
            pedirPermisosBluetooth();
            return;
        }

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
        this.elEscanner.startScan(this.callbackDelEscaneo);
    }

    // ---------------------
    private void detenerBusquedaDispositivosBTLE() {
        if (this.callbackDelEscaneo == null) {
            return;
        }
        if (this.elEscanner == null) {
            return;
        }
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
        this.elEscanner.stopScan(this.callbackDelEscaneo);
        this.callbackDelEscaneo = null;
    }

    // --------------------- botones UI (publicos) ---------------------
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton buscar dispositivos BTLE Pulsado" );
        this.buscarTodosLosDispositivosBTLE();
    }

    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton nuestro dispositivo BTLE Pulsado" );
        this.buscarEsteDispositivoBTLE("fistro");
    }

    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton detener busqueda dispositivos BTLE Pulsado" );
        this.detenerBusquedaDispositivosBTLE();
    }

    // --------------------- manejo respuesta permisos ---------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PETICION_PERMISOS) {
            Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): revisar permisos");
            // Si quieres, puedes comprobar grantResults aquí y volver a intentar iniciar escaneo
        }
    }
}
