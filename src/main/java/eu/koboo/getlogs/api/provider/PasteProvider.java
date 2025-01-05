package eu.koboo.getlogs.api.provider;

import org.jetbrains.annotations.NotNull;

public interface PasteProvider {

    String paste(@NotNull String logContent);
}
