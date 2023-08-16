package my.telegram_bot.utils;

import my.telegram_bot.service.enums.ServiceCommands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageUtils {
    public SendMessage generatedSendMessageWithText(Update update, String text) {
        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId( message.getChatId().toString() );
        sendMessage.setText( text );

        return sendMessage;
    }
    public SendMessage startMenu(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId( String.valueOf( chatId ) );
        sendMessage.setText( "Здравствуйте выберете пункт для начала работы" );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton calc_button = new InlineKeyboardButton();
        calc_button.setText( "Рассчитать стоимость" );
        calc_button.setCallbackData( ServiceCommands.CURRENCY.toString() );

        InlineKeyboardButton about_button = new InlineKeyboardButton();
        about_button.setText( "О боте" );
        about_button.setCallbackData( ServiceCommands.HELP.toString() );

        rowInline.add( calc_button );
        rowInline.add( about_button );

        rowsInline.add( rowInline );

        markup.setKeyboard( rowsInline );
        sendMessage.setReplyMarkup( markup );

        return sendMessage;
    }
}
