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

            if (checkCommandStartAndTimeout( user, message )) {
                log.debug( " Received command \" start \" " );
                user.setCommands( ServiceCommands.START );
                return ServiceCommands.START;
            }
            if (checkCommand( user, ServiceCommands.INNER_SUM )) {
                log.debug( " Received command \" INNER_SUM \" " );
                return ServiceCommands.INNER_SUM;
            }
            if (checkCommand( user, ServiceCommands.RISK )) {
                log.debug( " Received command \" RISK \" " );
                return ServiceCommands.RISK;
            }
            if (checkCommand( user, ServiceCommands.BALANCE )) {
                log.debug( " Received command \" BALANCE \" " );
                return ServiceCommands.BALANCE;
            }
        } else if (update.hasCallbackQuery()) {

            String callback = update.getCallbackQuery().getData();

            if (checkCallback( callback, ServiceCommands.HELP ) | checkCommand( user, ServiceCommands.HELP )) {
                log.debug( " Received command \" help \" " );
                user.setCommands( ServiceCommands.START );
                return ServiceCommands.HELP;
            }
            if (checkCallback( callback, ServiceCommands.CURRENCY ) &&
                    checkCommand( user, ServiceCommands.START )) {
                log.debug( " Received command \" currency \" " );
                user.setCommands( ServiceCommands.CURRENCY );
                return ServiceCommands.CURRENCY;
            }
            if ((checkCallback( callback, ServiceCommands.RUB ) &&
                    checkCommand( user, ServiceCommands.CURRENCY )) |
                    checkCommand( user, ServiceCommands.RUB )) {
                log.debug( " Received command \" RUB \" " );
                user.setCommands( ServiceCommands.RUB );
                return ServiceCommands.RUB;

            } else if ((checkCallback( callback, ServiceCommands.USD ) &&
                    checkCommand( user, ServiceCommands.CURRENCY )) |
                    checkCommand( user, ServiceCommands.USD )) {
                log.debug( " Received command \" USD \" " );
                user.setCommands( ServiceCommands.USD );
                return ServiceCommands.USD;

            } else if ((checkCallback( callback, ServiceCommands.BTC) &&
                    checkCommand( user, ServiceCommands.CURRENCY )) |
                    checkCommand( user, ServiceCommands.BTC )) {
                log.debug( " Received command \" BTC \" " );
                user.setCommands( ServiceCommands.BTC );
                return ServiceCommands.BTC;
            }
            return ServiceCommands.TIMEOUT;
        }
        return ServiceCommands.START;
    }

    private boolean checkCallback(String callback, ServiceCommands serviceCommands ) {
        return callback.equals( serviceCommands.toString() );
    }

    private boolean checkCommand(User user, ServiceCommands risk) {
        return user.getCommands().equals( risk );
    }

    private boolean checkCommandStartAndTimeout(User user, String message) {
        return checkCallback( message,ServiceCommands.START ) || checkCommand( user, ServiceCommands.START ) ||
                checkCommand( user, ServiceCommands.TIMEOUT );
    }
}
