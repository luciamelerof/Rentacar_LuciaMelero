package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExchangeRateService {

    private static final String API_KEY = "58941bea92fa35345b75557b";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    public static double convertir(double cantidad, String monedaDestino) {
        try {
            String urlStr = BASE_URL + API_KEY + "/pair/EUR/" + monedaDestino;
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream())
            );
            StringBuilder respuesta = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                respuesta.append(linea);
            }
            br.close();

            // Extraer conversion_rate del JSON manualmente
            String json = respuesta.toString();
            String buscar = "\"conversion_rate\":";
            int idx = json.indexOf(buscar);
            if (idx != -1) {
                String resto = json.substring(idx + buscar.length());
                String valorStr = resto.split("[,}]")[0].trim();
                double tasa = Double.parseDouble(valorStr);
                return Math.round(cantidad * tasa * 100.0) / 100.0;
            }

        } catch (Exception e) {
            System.err.println("Error al llamar a la API: " + e.getMessage());
        }
        return -1;
    }
}