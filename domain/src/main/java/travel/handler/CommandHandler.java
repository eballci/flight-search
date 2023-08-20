package travel.handler;

import travel.command.Command;

public interface CommandHandler<R, T extends Command> {
    R handle(T command);
}
