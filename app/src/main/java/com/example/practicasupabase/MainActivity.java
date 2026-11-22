package com.example.practicasupabase;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String REQUEST_TAG = "ALUMNOS_REQUEST";
    private static final String ENDPOINT =
            "/rest/v1/alumnos?select=id,nombres,paralelo,periodo,correo&order=id.asc";

    private TextView tvResultado;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        tvResultado = findViewById(R.id.tvResultado);
        requestQueue = Volley.newRequestQueue(this);

        cargarAlumnos();
    }

    /** Solicitud HTTP GET a la API REST de Supabase mediante Volley. */
    private void cargarAlumnos() {
        tvResultado.setText("Cargando alumnos...");

        String url = BuildConfig.SUPABASE_URL + ENDPOINT;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                this::mostrarAlumnos,
                this::manejarError) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", BuildConfig.SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + BuildConfig.SUPABASE_KEY);
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        request.setTag(REQUEST_TAG);
        requestQueue.add(request);
    }

    /** Procesa el arreglo JSON con JSONArray y JSONObject y arma el texto a mostrar. */
    private void mostrarAlumnos(JSONArray respuesta) {
        if (respuesta.length() == 0) {
            tvResultado.setText("No hay alumnos registrados en la tabla.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        try {
            for (int i = 0; i < respuesta.length(); i++) {
                JSONObject alumno = respuesta.getJSONObject(i);

                sb.append(i + 1).append(".- ")
                  .append(alumno.optString("nombres", "-")).append("\n")
                  .append("Paralelo: ").append(alumno.optString("paralelo", "-")).append("\n")
                  .append("Periodo: ").append(alumno.optString("periodo", "-")).append("\n")
                  .append("Email: ").append(alumno.optString("correo", "-")).append("\n\n");
            }
            tvResultado.setText(sb.toString());
        } catch (JSONException e) {
            tvResultado.setText("Error al procesar los datos recibidos: " + e.getMessage());
        }
    }

    /** Traduce los errores de Volley a mensajes comprensibles. */
    private void manejarError(VolleyError error) {
        String mensaje;
        if (error instanceof NoConnectionError) {
            mensaje = "Sin conexion a Internet. Verifique su red e intente nuevamente.";
        } else if (error instanceof TimeoutError) {
            mensaje = "Tiempo de espera agotado al contactar con el servidor.";
        } else if (error.networkResponse != null) {
            int codigo = error.networkResponse.statusCode;
            if (codigo == 401 || codigo == 403) {
                mensaje = "Acceso denegado (" + codigo + "). Revise la clave y las politicas RLS.";
            } else if (codigo == 404) {
                mensaje = "Endpoint no encontrado (404). Revise la URL y el nombre de la tabla.";
            } else {
                mensaje = "Error de la API. Codigo HTTP: " + codigo;
            }
        } else {
            mensaje = "Error inesperado: " + error.getMessage();
        }
        tvResultado.setText(mensaje);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUEST_TAG);
        }
    }
}
