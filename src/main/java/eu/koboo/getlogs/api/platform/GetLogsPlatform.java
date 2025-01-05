package eu.koboo.getlogs.api.platform;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface GetLogsPlatform {

    @NotNull String getPlatformName();

    @NotNull File resolveLatestLogFile();
}
