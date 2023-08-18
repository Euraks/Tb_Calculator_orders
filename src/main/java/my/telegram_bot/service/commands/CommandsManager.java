package my.telegram_bot.service.commands;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import my.telegram_bot.model.User;
import my.telegram_bot.service.UserService;
import my.telegram_bot.service.commands.enums.ServiceCommands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
@Log4j
@Component
public class CommandsManager implements BotCommands {
    private ServiceCommands serviceCommands;
    private final UserService userService;

    @Override
    public ServiceCommands get(Update update) {
        User user = userService.get( update );
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            if (message.equals( "/start" ) ||
                    user.getCommands().equals( ServiceCommands.START ) ||
                    user.getCommands().equals( ServiceCommands.TIMEOUT )) {
                log.debug( " Received command \" start \" " );
                user.setCommands( ServiceCommands.START );
                return ServiceCommands.START;
            }

        } else if (update.hasCallbackQuery()) {
            String callback = update.getCallbackQuery().getData();
            if (callback.equals( ServiceCommands.HELP.toString() ) | user.getCommands().equals( ServiceCommands.HELP )) {
                log.debug( " Received command \" help \" " );

                user.setCommands( ServiceCommands.START );

                return ServiceCommands.HELP;
            }
            if (callback.equals( ServiceCommands.CURRENCY.toString() ) &&
                    (user.getCommands().equals( ServiceCommands.START ))) {
                log.debug( " Received command \" currency \" " );

                user.setCommands( ServiceCommands.CURRENCY );

                return ServiceCommands.CURRENCY;
            }
            if ((callback.equals( ServiceCommands.RUB.toString() ) &&
                    (user.getCommands().equals( ServiceCommands.CURRENCY ))) |
                    user.getCommands().equals( ServiceCommands.RUB )) {
                log.debug( " Received command \" RUB \" " );

                user.setCommands( ServiceCommands.RUB );

                return ServiceCommands.RUB;
            } else if ((callback.equals( ServiceCommands.USD.toString() ) &&
                    (user.getCommands().equals( ServiceCommands.CURRENCY ))) |
                    user.getCommands().equals( ServiceCommands.USD )) {
                log.debug( " Received command \" USD \" " );

                user.setCommands( ServiceCommands.USD );

                return ServiceCommands.USD;
            } else if ((callback.equals( ServiceCommands.BTC.toString() ) &&
                    (user.getCommands().equals( ServiceCommands.CURRENCY ))) |
                    user.getCommands().equals( ServiceCommands.BTC )) {
                log.debug( " Received command \" BTC \" " );

                user.setCommands( ServiceCommands.BTC );

                return ServiceCommands.BTC;
            }
            return ServiceCommands.TIMEOUT;
        }
        return ServiceCommands.START;
    }
}
