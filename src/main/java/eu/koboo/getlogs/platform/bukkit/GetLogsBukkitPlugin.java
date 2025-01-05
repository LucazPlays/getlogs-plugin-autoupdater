package eu.koboo.getlogs.platform.bukkit;

import eu.koboo.getlogs.api.GetLogsAPI;
import eu.koboo.getlogs.api.GetLogsFactory;
import eu.koboo.getlogs.api.platform.GetLogsPlatform;
import eu.koboo.getlogs.api.provider.MCLogsPasteProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class GetLogsBukkitPlugin extends JavaPlugin implements GetLogsPlatform {

    @Getter
    GetLogsAPI api;

    @Override
    public void onEnable() {
        super.onEnable();
        this.api = GetLogsFactory.create(this, new MCLogsPasteProvider());
        PluginCommand command = getCommand("getlogs");
        if (command == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            log.info("Couldn't find command \"/getlogs\". Disabling plugin!");
            return;
        }
        command.setExecutor(new GetLogsBukkitCommand(api));
    }

    @Override
    public @NotNull String getPlatformName() {
        return "Bukkit";
    }

    @Override
    public @NotNull File resolveLatestLogFile() {
        return new File("logs/latest.log");
    }
}
