package eu.koboo.getlogs.api.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import eu.koboo.getlogs.api.httpclient.HttpClient;
import eu.koboo.getlogs.api.httpclient.HttpMethod;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class MCLogsPasteProvider implements PasteProvider {

    private static final String MC_LOGS_PASTE_URI = "https://api.mclo.gs/1/log";

    private static final String USER_AGENT_KEY = "User-Agent";
    // gets modified by kyori blossom.
    private static final String USER_AGENT_VALUE = "GetLogs-Client-v{{ getlogs_version }}";

    private static final String ACCEPT_KEY = "Accept";
    private static final String ACCEPT_VALUE = "application/json";

    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded; charset=UTF-8";

    private static final String BODY_KEY = "content=";

    //    HttpClient client;
    Gson gson;

    public MCLogsPasteProvider() {
//        this.client = HttpClient.newBuilder()
//                .connectTimeout(Duration.ofSeconds(30))
//                .cookieHandler(new CookieManager())
//                .followRedirects(HttpClient.Redirect.ALWAYS)
//                .build();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .create();
    }

    @Override
    public String paste(@NotNull String logContent) {
        String urlEncodedContent;
        try {
            urlEncodedContent = URLEncoder.encode(logContent, "UTF-8");
        } catch (IOException e) {
            log.error("Couldn't encode log content to url format: ", e);
            return null;
        }

        String bodyContent = BODY_KEY + urlEncodedContent;

        Map<String, String> headers = new HashMap<>();
        headers.put(USER_AGENT_KEY, USER_AGENT_VALUE);
        headers.put(ACCEPT_KEY, ACCEPT_VALUE);
        headers.put(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);

        String bodyString;
        try {
            bodyString = HttpClient.sendHttpRequest(MC_LOGS_PASTE_URI, HttpMethod.POST, headers, bodyContent);
        } catch (IOException e) {
            log.error("Couldn't request mclo.gs: ", e);
            return null;
        }

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(bodyString, JsonObject.class);
        } catch (JsonParseException e) {
            log.error("Couldn't parse body from mclo.gs: {}", bodyString, e);
            return null;
        }
        if (!jsonObject.has("url")) {
            log.error("Json misses url key: {}", bodyString);
            return null;
        }
        return jsonObject.get("url").getAsString();
    }
}
