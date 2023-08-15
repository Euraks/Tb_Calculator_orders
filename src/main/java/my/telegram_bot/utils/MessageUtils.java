package my.telegram_bot.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {
    public SendMessage generatedSendMessageWithText(Update update, String text) {
        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId( message.getChatId().toString() );
        sendMessage.setText( text );

        return sendMessage;
    }
}
