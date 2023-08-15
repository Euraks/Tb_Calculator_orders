package my.telegram_bot.service;

import my.telegram_bot.config.BotConfig;
import my.telegram_bot.model.BotUser;
import my.telegram_bot.model.Command;
import my.telegram_bot.model.Order;
import my.telegram_bot.repository.CommandRepository;
import my.telegram_bot.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String CALC_BUTTON = "CALC_BUTTON";
    private final String ABOUT_BUTTON = "ABOUT_BUTTON";
    private final String RUB_BUTTON = "RUB_BUTTON";
    private final String USD_BUTTON = "USD_BUTTON";
    private final String BTC_BUTTON = "BTC_BUTTON";


    private final BotConfig config;

    private final UserRepository userRepository;
    private final CommandRepository commandRepository;

    private final HashMap<Long, Order> userOrderMap = new HashMap<>();
    Order newOrder;


    public TelegramBot(BotConfig config, UserRepository repository, CommandRepository commandRepository) {
        this.config = config;
        this.userRepository = repository;
        this.commandRepository = commandRepository;
    }


    @Override
    public void onUpdateReceived(Update update) {
        long chatId;
        Long userId = getUserId( update );

        if (userRepository.findById( userId ).isEmpty()) {
            registerUser( update );
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            if (update.hasMessage() && (!update.getMessage().getText().equals( "/start" ))) {
                Order order = userOrderMap.get( chatId );
                if (order.getAmount() == 0.0) {
                    addAmountToOrder( update, order );
                    sendMessage( chatId, " Теперь введите процент риска от 0 до 100" );
                } else if (order.getRisk() == 0) {
                    addRiskToOrder( update, order );
                    sendMessage( chatId, " Ваш текущий баланс: " );
                } else if (order.getBalance() == 0) {
                    addBalanceToOrder( update, order );
                    String answer = getAnswerToOrder( order );
                    addResultUserToDataBase( update, answer );
                    sendMessage( chatId, answer );
                }
            }

            if (messageText.equalsIgnoreCase( "/start" )) {
                addCommandToUserPlusText( update, "/start" );
                startMenu( chatId );
            }

        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            processCallbackData( callbackData, chatId );
        }
    }


    private void registerUser(Update update) {
        User user = getMessage( update );
        BotUser newBotUser = new BotUser( user.getId(),
                user.getFirstName(),
                user.getIsBot(),
                user.getLastName(),
                user.getUserName(),
                user.getLanguageCode(),
                user.getCanJoinGroups(),
                user.getCanReadAllGroupMessages(),
                user.getSupportInlineQueries() );
        userRepository.save( newBotUser );
    }

    private User getMessage(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom();
        } else {
            return update.getCallbackQuery().getFrom();
        }
    }

    private Long getUserId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        } else {
            return update.getCallbackQuery().getFrom().getId();
        }
    }


    private void addCommandToUserPlusText(Update update, String text) {
        Long userId = update.getMessage().getFrom().getId();
        BotUser botUser = userRepository.findById( userId ).get();
        Message message = update.getMessage();
        Long chatId = update.getMessage().getChatId();
        Command command = new Command( message.getMessageId(), userId, text );
        commandRepository.save( command );
        botUser.getCommandList().remove( command );
        userRepository.save( botUser );

    }

    private void addResultUserToDataBase(Update update, String text) {
        Long userId = update.getMessage().getFrom().getId();
        BotUser botUser = userRepository.findById( userId ).get();
        Message message = update.getMessage();
        Long chatId = update.getMessage().getChatId();
        int randomId = message.getMessageId() + ThreadLocalRandom.current().nextInt( 0, 300 );
        Command command = new Command( randomId, userId, text );
        commandRepository.save( command );
        botUser.getCommandList().remove( command );
        userRepository.save( botUser );

    }

    private void processCallbackData(String callbackData, long chatId) {
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
                createNewOrder( RUB_BUTTON, chatId );
                break;
            case USD_BUTTON:
                sendMessage( chatId, "Выбрана валюта: USD. Введите сумму вхождения:" );
                createNewOrder( USD_BUTTON, chatId );
                break;
            case BTC_BUTTON:
                sendMessage( chatId, "Выбрана валюта: BTC. Введите сумму вхождения:" );
                createNewOrder( BTC_BUTTON, chatId );
                break;
        }
    }

    private void createNewOrder(String RUB_BUTTON, long chatId) {
        newOrder = new Order();
        newOrder.setMoney( RUB_BUTTON );
        userOrderMap.put( chatId, newOrder );
    }

    private String getAnswerToOrder(Order order) {
        return "Ваши данные \n" +
                "Сумма вхождения        : " + order.getAmount() + "\n" +
                "Процент риска          : " + order.getRisk() + "\n" +
                "Ваш баланс             : " + order.getBalance() + "\n" +
                "Результат расчета      : " + order.getCalc() + "\n";
    }

    private void addBalanceToOrder(Update update, Order order) {
        double balance = Double.parseDouble( update.getMessage().getText() );
        addCommandToUserPlusText( update, "Баланс : " + balance );
        order.setBalance( balance );
    }

    private void addRiskToOrder(Update update, Order order) {
        int risk = Integer.parseInt( update.getMessage().getText() );
        addCommandToUserPlusText( update, "Процент риска : " + risk );
        order.setRisk( risk );
    }

    private void addAmountToOrder(Update update, Order order) {
        double amount = Double.parseDouble( update.getMessage().getText() );
        order.setAmount( amount );
        addCommandToUserPlusText( update, "Сумма вхождения : " + amount );

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
