package eu.koboo.getlogs.platform.bungeecord;

import eu.koboo.getlogs.api.GetLogsAPI;
import eu.koboo.getlogs.api.GetLogsFactory;
import eu.koboo.getlogs.api.platform.GetLogsPlatform;
import eu.koboo.getlogs.api.provider.MCLogsPasteProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class GetLogsBungeecordPlugin extends Plugin implements GetLogsPlatform {

    @Getter
    GetLogsAPI api;

    @Override
    public void onEnable() {
        super.onEnable();
        this.api = GetLogsFactory.create(this, new MCLogsPasteProvider());
        GetLogsBungeecordCommand command = new GetLogsBungeecordCommand(api);
        getProxy().getPluginManager().registerCommand(this, command);
    }

    @Override
    public @NotNull String getPlatformName() {
        return "Bungeecord";
    }

    @Override
    public @NotNull File resolveLatestLogFile() {
        // BungeeCord
        if(ProxyServer.getInstance().getName().equals("BungeeCord")) {
            return new File("proxy.log.0");
        }
        // Waterfall
        return new File("logs/latest.log");
    }
}
