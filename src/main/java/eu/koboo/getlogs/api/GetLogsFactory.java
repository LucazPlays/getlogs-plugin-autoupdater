package eu.koboo.getlogs.api;

import eu.koboo.getlogs.api.platform.GetLogsPlatform;
import eu.koboo.getlogs.api.provider.PasteProvider;
import org.jetbrains.annotations.NotNull;

public final class GetLogsFactory {

    public static @NotNull GetLogsAPI create(@NotNull GetLogsPlatform platform,
                                             @NotNull PasteProvider provider) {
        return new GetLogsAPI(platform, provider);
    }
}
