package my.telegram_bot.service;

import lombok.extern.log4j.Log4j;
import my.telegram_bot.model.User;
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
                String originalMessage = update.getMessage().getText();
                User user = userService.get( update );
                log.debug( user );
                if (user.getCommands().equals( "/start" )) {
                    SendMessage response = messageUtils.startMenu( update );
                    sendMessage( response );
                }
            }
        }
    }

    private void sendMessage(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage( sendMessage );
    }
}
