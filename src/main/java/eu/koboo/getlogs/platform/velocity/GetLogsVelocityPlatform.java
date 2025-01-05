package eu.koboo.getlogs.platform.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.koboo.getlogs.api.GetLogsAPI;
import eu.koboo.getlogs.api.GetLogsFactory;
import eu.koboo.getlogs.api.platform.GetLogsPlatform;
import eu.koboo.getlogs.api.provider.MCLogsPasteProvider;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

@Plugin(
        id = "getlogs-velocity",
        name = "getlogs_name",
        version = "getlogs_version",
        authors = "getlogs_author"
)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class GetLogsVelocityPlatform implements GetLogsPlatform {

    ProxyServer proxyServer;
    Logger pluginLogger;

    GetLogsAPI api;

    @Inject
    public GetLogsVelocityPlatform(ProxyServer proxyServer, Logger pluginLogger) {
        this.proxyServer = proxyServer;
        this.pluginLogger = pluginLogger;
        this.api = GetLogsFactory.create(this, new MCLogsPasteProvider());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxyServer.getCommandManager().register("getlogsvelocity", new GetLogsVelocityCommand(api));
    }

    @Override
    public @NotNull String getPlatformName() {
        return "Velocity";
    }

    @Override
    public @NotNull File resolveLatestLogFile() {
        return new File("logs/latest.log");
    }
}
