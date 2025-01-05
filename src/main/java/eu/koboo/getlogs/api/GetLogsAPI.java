package eu.koboo.getlogs.api;

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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public final class GetLogsAPI {

    public static final String BASE_COMMAND_PERMISSION = "getlogs.command.";

    GetLogsPlatform platform;
    PasteProvider pasteProvider;

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
        CompletableFuture<Result<String, Object>> pasteFuture = pasteProvider.paste(logContent);
        Result<String, Object> result;
        try {
            result = pasteFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Couldn't resolve paste future: ", e);
            return null;
        }
        if (result.hasError()) {
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
        return readStringFromFile(latestLogs);
    }

    private @Nullable String readStringFromFile(@NotNull File file) {
        // We need to do it that way, because we want jdk 8 support in future releases.
        // Files.readAllLines is not existent there.
        List<String> contentLines;
        try {
            contentLines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Could not read latest log file {}", file.getAbsolutePath(), e);
            return null;
        }
        StringBuilder content = new StringBuilder();
        for (String line : contentLines) {
            content.append(line);
        }
        return content.toString();
    }
}
