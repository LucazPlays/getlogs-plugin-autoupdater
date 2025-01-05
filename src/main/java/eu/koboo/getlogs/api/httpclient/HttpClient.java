package eu.koboo.getlogs.api.httpclient;

import lombok.experimental.UtilityClass;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@UtilityClass
public class HttpClient {

    /**
     * Sends an HTTP request to the given endpoint with the specified method and body.
     *
     * @param method The HTTP method to use.
     * @param body   The body of the request (can be null).
     * @return The response from the server.
     * @throws IOException If an error occurs during the request.
     */
    public String sendHttpRequest(String uri, HttpMethod method, Map<String, String> headers, String body) throws IOException {
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method.name());
        for (String headerKey : headers.keySet()) {
            connection.setRequestProperty(headerKey, headers.get(headerKey));
        }

        if (body != null && !body.isEmpty()) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }

        int responseCode = connection.getResponseCode();
        InputStream is = (responseCode >= 200 && responseCode < 300) ?
                connection.getInputStream() : connection.getErrorStream();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }
}
