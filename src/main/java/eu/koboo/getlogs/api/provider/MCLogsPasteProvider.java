package eu.koboo.getlogs.api.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import eu.koboo.getlogs.api.result.Result;
import eu.koboo.getlogs.api.result.errors.ExceptionResult;
import eu.koboo.getlogs.api.result.errors.InvalidResponseResult;
import eu.koboo.getlogs.api.result.errors.NoResponseResult;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

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

    HttpClient client;
    Gson gson;

    public MCLogsPasteProvider() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .cookieHandler(new CookieManager())
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .create();
    }

    @Override
    public CompletableFuture<Result<String, Object>> paste(@NotNull String logContent) {
        CompletableFuture<Result<String, Object>> resultFuture = new CompletableFuture<>();

        String urlEncodedContent  = URLEncoder.encode(logContent, StandardCharsets.UTF_8);

        String bodyContent = BODY_KEY + urlEncodedContent;

        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(MC_LOGS_PASTE_URI))
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(bodyContent))
                .header(USER_AGENT_KEY, USER_AGENT_VALUE)
                .header(ACCEPT_KEY, ACCEPT_VALUE)
                .header(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);

        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(
                builder.build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        responseFuture.whenComplete((response, exception) -> {
            if (exception != null) {
                resultFuture.complete(Result.error(new ExceptionResult(exception)));
                return;
            }
            String bodyString = response.body();
            if (bodyString == null || bodyString.isEmpty()) {
                resultFuture.complete(Result.error(new NoResponseResult()));
                return;
            }
            JsonObject jsonObject;
            try {
                jsonObject = gson.fromJson(bodyString, JsonObject.class);
            } catch (JsonParseException e) {
                resultFuture.complete(Result.error(new ExceptionResult(e)));
                return;
            }
            if (!jsonObject.has("url")) {
                resultFuture.complete(Result.error(new InvalidResponseResult()));
                return;
            }
            String showUri = jsonObject.get("url").getAsString();
            resultFuture.complete(Result.success(showUri));
        });

        return resultFuture;
    }
}
