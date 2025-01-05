package eu.koboo.getlogs.platform.velocity;

import com.velocitypowered.api.command.RawCommand;
import eu.koboo.getlogs.api.GetLogsAPI;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class GetLogsVelocityCommand implements RawCommand {

    GetLogsAPI api;

    @Override
    public void execute(Invocation invocation) {
        String showUrl = api.pasteLatestLogs();
        if (showUrl == null) {
            invocation.source().sendMessage(Component.text(api.getEventNoUrl()));
            return;
        }
        invocation.source().sendMessage(Component.text(api.getEventShowUrl().replace("%url%", showUrl)));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission(api.getCommandPermission());
    }
}
