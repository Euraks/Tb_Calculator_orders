package my.telegram_bot.service;

import lombok.extern.log4j.Log4j;
import my.telegram_bot.service.commands.CommandsManager;
import my.telegram_bot.service.commands.enums.ServiceCommands;
import my.telegram_bot.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@Component
public class UpdateController {

    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final CommandsManager commandsManager;

    public UpdateController(MessageUtils messageUtils, CommandsManager commandsManager) {
        this.messageUtils = messageUtils;


        this.commandsManager = commandsManager;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error( " Received update is null" );
        } else {
            ServiceCommands serviceCommands = commandsManager.get( update );
            if (serviceCommands.equals( ServiceCommands.START )) {
                startMenu( update );
            }
            if (serviceCommands.equals( ServiceCommands.HELP )) {
                infoMenu( update );
            }
            if (serviceCommands.equals( ServiceCommands.CURRENCY )) {
                currencyMenuOption( update );
            }
            if (serviceCommands.equals( ServiceCommands.RUB )) {
                createNewOrder( ServiceCommands.RUB, update );
            }
            if (serviceCommands.equals( ServiceCommands.USD )) {
                createNewOrder( ServiceCommands.USD, update );
            }
            if (serviceCommands.equals( ServiceCommands.BTC )) {
                createNewOrder( ServiceCommands.BTC, update );
            }
            if (serviceCommands.equals( ServiceCommands.INNER_SUM )) {
                setInnerSum( update );
            }
            if (serviceCommands.equals( ServiceCommands.RISK )) {
                setRisk( update );
            }
            if (serviceCommands.equals( ServiceCommands.BALANCE )) {
                setBalance( update );
            }
            if (serviceCommands.equals( ServiceCommands.TIMEOUT )) {
                timeOut( update );
            }
        }
    }

    private void timeOut(Update update) {
        SendMessage response = messageUtils.timeoutMessage( update );
        sendMessage( response );
    }

    private void setBalance(Update update) {
        SendMessage response = messageUtils.setBalance( update );
        sendMessage( response );
    }

    private void setRisk(Update update) {
        SendMessage response = messageUtils.setRisk( update );
        sendMessage( response );
    }

    private void setInnerSum(Update update) {
        SendMessage response = messageUtils.setInnerSum( update );
        sendMessage( response );
    }


    private void createNewOrder(ServiceCommands command, Update update) {
        SendMessage response = messageUtils.createNewOrder( command, update );
        sendMessage( response );
    }

    private void currencyMenuOption(Update update) {
        SendMessage response = messageUtils.currencyMenu( update );
        sendMessage( response );
    }

    private void infoMenu(Update update) {
        SendMessage response = messageUtils.infoMenu( update );
        sendMessage( response );

    }

    private void startMenu(Update update) {
        SendMessage response = messageUtils.startMenu( update );
        sendMessage( response );
    }

    private void sendMessage(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage( sendMessage );
    }
}
