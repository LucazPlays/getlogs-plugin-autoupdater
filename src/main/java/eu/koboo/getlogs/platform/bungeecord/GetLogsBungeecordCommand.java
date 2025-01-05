package eu.koboo.getlogs.platform.bungeecord;

import eu.koboo.getlogs.api.GetLogsAPI;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class GetLogsBungeecordCommand extends Command {

    GetLogsAPI api;

    public GetLogsBungeecordCommand(GetLogsAPI api) {
        super("getlogsproxy", api.getCommandPermission());
        this.api = api;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!commandSender.hasPermission(api.getCommandPermission())) {
            return;
        }
        String showUrl = api.pasteLatestLogs();
        if (showUrl == null) {
            commandSender.sendMessage(api.getEventNoUrl());
            return;
        }
        commandSender.sendMessage(api.getEventShowUrl().replace("%url%", showUrl));
    }
}
