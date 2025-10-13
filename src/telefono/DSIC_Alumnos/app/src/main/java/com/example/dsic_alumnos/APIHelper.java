package com.example.dsic_alumnos;

import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIHelper {

    private static final String ETIQUETA_LOG = "APIHelper";
    // URL apuntando a tu PC + puerto
    private static final String SERVER_URL = "http://10.97.245.133:8080/insertar";

    public static void enviarMedicion(BeaconData bd) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                JSONObject json = new JSONObject();
                json.put("mac", bd.getMac());
                json.put("major", bd.getMajor());
                json.put("minor", bd.getMinor());
                json.put("txPower", bd.getTxPower());
                json.put("timestamp", bd.getTimestamp());

                URL url = new URL(SERVER_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                writer.write(json.toString());
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                Log.d(ETIQUETA_LOG, "Respuesta servidor REST: " + responseCode);
            } catch (Exception e) {
                Log.e(ETIQUETA_LOG, "Error enviando JSON: " + e.getMessage(), e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
