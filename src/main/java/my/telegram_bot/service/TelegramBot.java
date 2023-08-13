package my.telegram_bot.service;

import my.telegram_bot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private PeopleService service;

    private final BotConfig config;
    private boolean waitingForResponse = false;
    private long waitingChatId;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equalsIgnoreCase( "/start" )) {
                sendResponse( chatId, "Привет! Ответьте на вопрос: Какой ваш любимый язык программирования?" );
                waitingForResponse = true;
                waitingChatId = chatId;
            } else if (waitingForResponse && chatId == waitingChatId) {
                sendResponse( chatId, "Спасибо за ответ: " + messageText );
                waitingForResponse = false;
            }
        }
    }

    private void sendResponse(long chatId, String message) {
        sendMessage( chatId, message );
    }

    private void sendMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId( String.valueOf( chatId ) );
        sendMessage.setText( message );
        try{
            execute( sendMessage );
        } catch(TelegramApiException e){
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
