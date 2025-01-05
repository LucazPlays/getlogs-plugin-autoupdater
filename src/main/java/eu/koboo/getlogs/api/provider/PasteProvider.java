package eu.koboo.getlogs.api.provider;

import eu.koboo.getlogs.api.result.Result;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface PasteProvider {

    CompletableFuture<Result<String, Object>> paste(@NotNull String logContent);
}
