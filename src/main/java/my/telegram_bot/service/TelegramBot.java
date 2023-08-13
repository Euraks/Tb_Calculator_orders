package my.telegram_bot.service;

import my.telegram_bot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String CALC_BUTTON = "CALC_BUTTON";
    private final String ABOUT_BUTTON = "ABOUT_BUTTON";
    private final String RUB_BUTTON = "RUB_BUTTON";
    private final String USD_BUTTON = "USD_BUTTON";
    private final String BTC_BUTTON = "BTC_BUTTON";

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
                startMenu( chatId );
            }

            if (messageText.equalsIgnoreCase( "/wait" )) {
                sendMessage( chatId, "Привет! Ответьте на вопрос: Какой ваш любимый язык программирования?" );
                waitingForResponse = true;
                waitingChatId = chatId;
            } else if (waitingForResponse && chatId == waitingChatId) {
                sendMessage( chatId, "Спасибо за рубли: " + messageText );
                waitingForResponse = false;
            }

        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            switch (callbackData) {
                case CALC_BUTTON:
                    sendMessage( chatId, "Вы нажали расчет" );
                    choseMoney( chatId );
                    break;
                case ABOUT_BUTTON:
                    sendMessage( chatId, "Вы нажали о боте" );
                    break;
                case RUB_BUTTON:
                    sendMessage(chatId, "Выбрана валюта: RUB. Введите сумму вхождения:");

                    break;
                case USD_BUTTON:
                    sendMessage( chatId, "USD_BUTTON нажата" );
                    break;
                case BTC_BUTTON:
                    sendMessage( chatId, "BTC_BUTTON нажата" );
                    break;
            }
        }
    }


    private void choseMoney(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId( String.valueOf( chatId ) );
        sendMessage.setText( "Здравствуйте выберете валюту в которой будет совершаться сделка" );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton RUB_button = new InlineKeyboardButton();
        RUB_button.setText( "RUB" );
        RUB_button.setCallbackData( RUB_BUTTON );

        InlineKeyboardButton USD_button = new InlineKeyboardButton();
        USD_button.setText( "USD" );
        USD_button.setCallbackData( USD_BUTTON );

        InlineKeyboardButton BTC_button = new InlineKeyboardButton();
        BTC_button.setText( "BTC" );
        BTC_button.setCallbackData( BTC_BUTTON );

        rowInline.add( RUB_button );
        rowInline.add( USD_button );
        rowInline.add( BTC_button );

        rowsInline.add( rowInline );

        markup.setKeyboard( rowsInline );
        sendMessage.setReplyMarkup( markup );

        try{
            execute( sendMessage );
        } catch(TelegramApiException e){
            System.out.println( e.getMessage() );
        }

    }

    private void startMenu(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId( String.valueOf( chatId ) );
        sendMessage.setText( "Здравствуйте выберете пункт для начала работы" );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton calc_button = new InlineKeyboardButton();
        calc_button.setText( "Рассчитать стоимость" );
        calc_button.setCallbackData( CALC_BUTTON );

        InlineKeyboardButton about_button = new InlineKeyboardButton();
        about_button.setText( "О боте" );
        about_button.setCallbackData( ABOUT_BUTTON );

        rowInline.add( calc_button );
        rowInline.add( about_button );

        rowsInline.add( rowInline );

        markup.setKeyboard( rowsInline );
        sendMessage.setReplyMarkup( markup );

        try{
            execute( sendMessage );
        } catch(TelegramApiException e){
            System.out.println( e.getMessage() );
        }
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
