package my.telegram_bot.service.commands;

import lombok.Data;
import my.telegram_bot.model.User;
import my.telegram_bot.service.UserService;
import my.telegram_bot.service.commands.enums.ServiceCommands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
@Component
public class CommandsManager implements BotCommands {
    private ServiceCommands serviceCommands;
    private final UserService userService;

    @Override
    public ServiceCommands get(Update update) {
        User user = userService.get( update );
        if (update.hasMessage() & update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            if (message.equals( "/start" ) ||
                    user.getCommands().equals( ServiceCommands.START )||
                    user.getCommands().equals( ServiceCommands.TIMEOUT )) {
                return ServiceCommands.START;
            }

        } else if (update.hasCallbackQuery()) {

        }
        return ServiceCommands.START;
    }
}
