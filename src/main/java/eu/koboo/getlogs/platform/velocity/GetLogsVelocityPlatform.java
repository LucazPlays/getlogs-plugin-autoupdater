package eu.koboo.getlogs.platform.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.net.ProxySelector;
import java.util.logging.Logger;

@Plugin(
        id = "getlogs-velocity",
        name = "{{ getlogs_name }}",
        version = "{{ getlogs_version }}",
        authors = "{{ getlogs_author }}"
)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetLogsVelocityPlatform {

    ProxySelector proxyServer;
    Logger pluginLogger;

    @Inject
    public GetLogsVelocityPlatform(ProxySelector proxyServer, Logger pluginLogger) {
        this.proxyServer = proxyServer;
        this.pluginLogger = pluginLogger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

    }
}
