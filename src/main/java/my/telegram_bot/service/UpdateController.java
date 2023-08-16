package my.telegram_bot.service;

import lombok.extern.log4j.Log4j;
import my.telegram_bot.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@Component
public class UpdateController {

    private TelegramBot telegramBot;

    public UpdateController(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    private final MessageUtils messageUtils;

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error( " Received update is null" );
        } else {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String originalMessage = update.getMessage().getText();
                if (originalMessage.equals( "/start" )) {
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
