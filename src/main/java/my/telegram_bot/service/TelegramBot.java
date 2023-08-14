package my.telegram_bot.service;

import my.telegram_bot.config.BotConfig;
import my.telegram_bot.model.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String CALC_BUTTON = "CALC_BUTTON";
    private final String ABOUT_BUTTON = "ABOUT_BUTTON";
    private final String RUB_BUTTON = "RUB_BUTTON";
    private final String USD_BUTTON = "USD_BUTTON";
    private final String BTC_BUTTON = "BTC_BUTTON";


    private final BotConfig config;

    private HashMap<Long, Order> userOrderMap = new HashMap<>();
    Order newOrder;

    private HashMap<Long, ArrayList<String>> userMap = new HashMap<>();
    private ArrayList<Long> listOfUsers = new ArrayList<>();

    public TelegramBot(BotConfig config) {
        this.config = config;
    }


    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (!listOfUsers.contains( chatId )) {
                listOfUsers.add( chatId );
            } else {

                ArrayList<String> answerList = new ArrayList<>();
                if (update.hasMessage()&&(!update.getMessage().getText().equals( "/start" ))) {
                    Order order = userOrderMap.get( chatId );
                    if (order.getAmount() == 0.0) {
                        double amount = Double.parseDouble( update.getMessage().getText() );
                        order.setAmount( amount );
                        sendMessage( chatId, " Теперь введите процент риска от 0 до 100" );
                    } else if (order.getRisk() == 0) {
                        int risk = Integer.parseInt( update.getMessage().getText() );
                        order.setRisk( risk );
                        sendMessage( chatId, " Ваш текущий баланс: " );
                    } else if (order.getBalance() == 0) {
                        double balance = Double.parseDouble( update.getMessage().getText() );
                        order.setBalance( balance );
                        String answer = "Ваши данные \n" +
                                "Сумма вхождения        : " + order.getAmount() + "\n" +
                                "Процент риска          : " + order.getRisk() + "\n" +
                                "Ваш баланс             : " + order.getBalance() + "\n" +
                                "Результат расчета      : " + order.getCalc() + "\n";
                        sendMessage( chatId, answer );
                    }

//                    answerList.add( update.getMessage().getText() );
                }
//                userMap.put( chatId, answerList );

            }

            if (messageText.equalsIgnoreCase( "/start" )) {
                startMenu( chatId );
            }

        } else if (update.hasCallbackQuery()) {

            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            ArrayList<String> answerList = new ArrayList<>();
            answerList.add( update.getCallbackQuery().getData() );
            userMap.put( chatId, answerList );


            switch (callbackData) {
                case CALC_BUTTON:
                    sendMessage( chatId, "Вы нажали расчет" );
                    choseMoney( chatId );
                    break;
                case ABOUT_BUTTON:
                    sendMessage( chatId, "Вы нажали о боте" );
                    String message = " Бот создан для расчета рисков по формуле \n" +
                            " Краткая инструкция: \n" +
                            " Для начала введите команду /start \n" +
                            " Далее отвечайте на вопросы и последовательно вводите данные \n" +
                            " Спасибо. \n";
                    sendMessage( chatId, message );
                    break;
                case RUB_BUTTON:
                    sendMessage( chatId, "Выбрана валюта: RUB. Введите сумму вхождения:" );
                    newOrder = new Order();
                    newOrder.setMoney( RUB_BUTTON );
                    userOrderMap.put( chatId, newOrder );
                    break;
                case USD_BUTTON:
                    sendMessage( chatId, "Выбрана валюта: USD. Введите сумму вхождения:" );
                    newOrder = new Order();
                    newOrder.setMoney( USD_BUTTON );
                    userOrderMap.put( chatId, newOrder );
                    break;
                case BTC_BUTTON:
                    sendMessage( chatId, "Выбрана валюта: BTC. Введите сумму вхождения:" );
                    newOrder = new Order();
                    newOrder.setMoney( BTC_BUTTON );
                    userOrderMap.put( chatId, newOrder );
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
