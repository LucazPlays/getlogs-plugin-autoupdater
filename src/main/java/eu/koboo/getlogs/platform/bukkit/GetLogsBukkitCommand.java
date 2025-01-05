package eu.koboo.getlogs.platform.bukkit;

import eu.koboo.getlogs.api.GetLogsAPI;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class GetLogsBukkitCommand implements CommandExecutor {

    GetLogsAPI api;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(commandSender.hasPermission(api.getCommandPermission())) {
            return false;
        }
        String showUrl = api.pasteLatestLogs();
        if(showUrl == null) {
            commandSender.sendMessage(api.getEventNoUrl());
            return true;
        }
        commandSender.sendMessage(api.getEventShowUrl().replace("%url%", showUrl));
        return true;
    }
}
