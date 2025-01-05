package eu.koboo.getlogs.platform.bungeecord;

import eu.koboo.getlogs.api.GetLogsAPI;
import eu.koboo.getlogs.api.GetLogsFactory;
import eu.koboo.getlogs.api.platform.GetLogsPlatform;
import eu.koboo.getlogs.api.provider.MCLogsPasteProvider;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class GetLogsBungeecordPlugin extends Plugin implements GetLogsPlatform {

    @Override
    public void onEnable() {
        super.onEnable();
        GetLogsAPI api = GetLogsFactory.create(this, new MCLogsPasteProvider());
        GetLogsBungeecordCommand command = new GetLogsBungeecordCommand(api);
        getProxy().getPluginManager().registerCommand(this, command);
    }

    @Override
    public @NotNull String getPlatformName() {
        return "Bungeecord";
    }

    @Override
    public @NotNull File resolveLatestLogFile() {
        return new File("logs/latest.log");
    }
}
