package my.telegram_bot.service.commands;

import my.telegram_bot.model.User;
import my.telegram_bot.service.commands.enums.ServiceCommands;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotCommands {
    ServiceCommands get(Update update);
}
