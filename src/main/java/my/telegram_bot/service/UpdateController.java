package my.telegram_bot.service;

import lombok.extern.log4j.Log4j;
import my.telegram_bot.model.User;
import my.telegram_bot.service.enums.ServiceCommands;
import my.telegram_bot.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@Component
public class UpdateController {

    private TelegramBot telegramBot;

    public UpdateController(MessageUtils messageUtils, UserService userService) {
        this.messageUtils = messageUtils;
        this.userService = userService;
    }

    private final MessageUtils messageUtils;
    private final UserService userService;

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error( " Received update is null" );
        } else {
            if (update.hasMessage() && update.getMessage().hasText()) {
                User user = userService.get( update );
                if (user.getCommands().equals( ServiceCommands.START ) | update.getMessage().getText().equals( "/start" )) {
                    log.debug( " Received command \" /start\" " );
                    startMenu( update );
                }
            } else if (update.hasCallbackQuery()) {
                processCallbackData( update );
            }
        }
    }

    private void processCallbackData(Update update) {
        User user = userService.get( update );
        String callback = update.getCallbackQuery().getData();
        if (callback.equals( ServiceCommands.HELP.toString() ) | user.getCommands().equals( ServiceCommands.HELP )) {
            log.debug( " Received command \" help \" " );
            user.setCommands( ServiceCommands.HELP );
            log.debug( "User " + user.getId() + " setCommands " + ServiceCommands.HELP );
            infoMenu( update );
        } else if ((callback.equals( ServiceCommands.CURRENCY.toString() ) &&
                (user.getCommands().equals( ServiceCommands.START ))) |
                user.getCommands().equals( ServiceCommands.CURRENCY )) {
            log.debug( " Received command \" currency \" " );
            user.setCommands( ServiceCommands.CURRENCY );
            currencyMenuOption( update );
        } else if ((callback.equals( ServiceCommands.RUB.toString() ) &&
                (user.getCommands().equals( ServiceCommands.START ))) |
                user.getCommands().equals( ServiceCommands.RUB )) {
            log.debug( " Received command \" RUB \" " );
            user.setCommands( ServiceCommands.RUB );
            createNewOrder( ServiceCommands.RUB, update );
        }
    }

    private void createNewOrder(ServiceCommands command, Update update) {
        SendMessage response = messageUtils.createNewOrder( command, update );
        sendMessage( response );
    }

    private void currencyMenuOption(Update update) {
        SendMessage response = messageUtils.currencyMenu( update );
        sendMessage( response );
        User user = userService.get( update );
        user.setCommands( ServiceCommands.START );
        log.debug( "User " + user.getId() + " setCommands " + ServiceCommands.START );
    }

    private void infoMenu(Update update) {
        SendMessage response = messageUtils.infoMenu( update );
        sendMessage( response );
        User user = userService.get( update );
        user.setCommands( ServiceCommands.START );
        log.debug( "User " + user.getId() + " setCommands " + ServiceCommands.START );
    }

    private void startMenu(Update update) {
        SendMessage response = messageUtils.startMenu( update );
        sendMessage( response );
    }

    private void sendMessage(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage( sendMessage );
    }
}
