package eu.koboo.getlogs.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.koboo.getlogs.api.platform.GetLogsPlatform;
import eu.koboo.getlogs.api.provider.PasteProvider;
import eu.koboo.getlogs.api.result.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public final class GetLogsAPI {

    // gets modified by kyori blossom.
    static final String GLOBAL_USER_AGENT = "GetLogs-Client-v{{ getlogs_version }}";
    static final String BASE_COMMAND_PERMISSION = "getlogs.command.";

    GetLogsPlatform platform;
    PasteProvider pasteProvider;
    HttpClient client;
    Gson gson;

    @Getter
    String platformName;
    @Getter
    String commandPermission;

    @Getter
    String eventNoUrl;
    @Getter
    String eventShowUrl;

    GetLogsAPI(@NotNull GetLogsPlatform platform, @NotNull PasteProvider pasteProvider) {
        this.platform = platform;
        this.pasteProvider = pasteProvider;
        this.client = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .create();

        this.platformName = platform.getPlatformName();
        this.commandPermission = BASE_COMMAND_PERMISSION + platformName.toLowerCase(Locale.ROOT);

        this.eventNoUrl = "Couldn't paste latest logs. Please check the server console for further information.";
        this.eventShowUrl = "Show your logs here: %url%";
    }

    public @Nullable String pasteLatestLogs() {
        String logContent = readLatestLogFile();
        if (logContent == null) {
            return null;
        }
        CompletableFuture<Result<String, Object>> pasteFuture = pasteProvider.paste(
                client,
                gson,
                GLOBAL_USER_AGENT,
                logContent);
        Result<String, Object> result;
        try {
            result = pasteFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Couldn't resolve paste future: ", e);
            return null;
        }
        if(result.hasError()) {
            log.error("Couldn't paste log file: {}", result.getError().getClass().getSimpleName());
            return null;
        }
        return result.getValue();
    }

    private @Nullable String readLatestLogFile() {
        File latestLogs = platform.resolveLatestLogFile();
        if (!latestLogs.exists()) {
            log.info("Latest log file doesn't exist {}", latestLogs.getAbsolutePath());
            return null;
        }
        String logContent;
        try {
            logContent = Files.readString(latestLogs.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Could not read latest log file {}", latestLogs.getAbsolutePath(), e);
            return null;
        }
        return logContent;
    }
}
