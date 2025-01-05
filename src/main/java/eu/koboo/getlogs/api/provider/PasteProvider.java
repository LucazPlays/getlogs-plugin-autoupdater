package eu.koboo.getlogs.api.provider;

import com.google.gson.Gson;
import eu.koboo.getlogs.api.result.Result;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface PasteProvider {

    CompletableFuture<Result<String, Object>> paste(@NotNull HttpClient client,
                                                    @NotNull Gson gson,
                                                    @NotNull String userAgent,
                                                    @NotNull String logContent);

    default HttpRequest.Builder buildRequest(String uriString) {
        return HttpRequest.newBuilder(URI.create(uriString))
                .timeout(Duration.ofSeconds(30));
    }
}
