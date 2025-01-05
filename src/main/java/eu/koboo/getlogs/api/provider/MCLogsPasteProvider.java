package eu.koboo.getlogs.api.provider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import eu.koboo.getlogs.api.result.Result;
import eu.koboo.getlogs.api.result.errors.ExceptionResult;
import eu.koboo.getlogs.api.result.errors.InvalidResponseResult;
import eu.koboo.getlogs.api.result.errors.NoResponseResult;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public final class MCLogsPasteProvider implements PasteProvider {

    private static final String MC_LOGS_PASTE_ENDPOINT = "https://api.mclo.gs";

    private static final String USER_AGENT_KEY = "User-Agent";
    private static final String ACCEPT_KEY = "Accept";
    private static final String ACCEPT_VALUE = "application/json";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded; charset=UTF-8";

    private static final String BODY_KEY = "content=";

    @Override
    public CompletableFuture<Result<String, Object>> paste(@NotNull HttpClient client,
                                                           @NotNull Gson gson,
                                                           @NotNull String userAgent,
                                                           @NotNull String logContent) {

        String urlEncodedBody = URLEncoder.encode(logContent, StandardCharsets.UTF_8);

        HttpRequest.Builder builder = buildRequest(MC_LOGS_PASTE_ENDPOINT + "/1/log")
                .POST(HttpRequest.BodyPublishers.ofString(BODY_KEY + urlEncodedBody))
                .header(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE)
                .header(ACCEPT_KEY, ACCEPT_VALUE)
                .header(USER_AGENT_KEY, userAgent);

        CompletableFuture<Result<String, Object>> resultFuture = new CompletableFuture<>();

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
