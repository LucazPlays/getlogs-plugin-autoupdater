package eu.koboo.getlogs.platform.velocity;

import com.velocitypowered.api.command.RawCommand;
import eu.koboo.getlogs.api.GetLogsAPI;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetLogsVelocityCommand implements RawCommand {

    GetLogsAPI api;

    @Override
    public void execute(Invocation invocation) {

    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return RawCommand.super.hasPermission(invocation);
    }
}
