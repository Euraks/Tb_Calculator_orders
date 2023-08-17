package my.telegram_bot.utils;

import lombok.extern.log4j.Log4j;
import my.telegram_bot.model.Order;
import my.telegram_bot.model.User;
import my.telegram_bot.service.UserService;
import my.telegram_bot.service.enums.ServiceCommands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Log4j
@Component
public class MessageUtils {

    private final UserService userService;

    public MessageUtils(UserService userService) {
        this.userService = userService;
    }

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

        return startMenu( update, sendMessage );
    }

    public SendMessage infoMenu(Update update) {
        log.debug( "Info Menu " );
        Long chatId = update.getCallbackQuery().getFrom().getId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId( String.valueOf( chatId ) );
        String message = " Бот создан для расчета рисков по формуле \n" +
                " Краткая инструкция: \n" +
                " Для начала введите команду /start \n" +
                " Далее отвечайте на вопросы и последовательно вводите данные \n" +
                " Спасибо. \n";
        sendMessage.setText( message );
        log.debug( "Info Menu view - ok" );

        return sendMessage;
    }

    public SendMessage currencyMenu(Update update) {
        Long chatId = update.getCallbackQuery().getFrom().getId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId( String.valueOf( chatId ) );
        sendMessage.setText( "Здравствуйте выберете валюту в которой будет совершаться сделка" );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton RUB_button = new InlineKeyboardButton();
        RUB_button.setText( "RUB" );
        RUB_button.setCallbackData( ServiceCommands.RUB.toString() );

        InlineKeyboardButton USD_button = new InlineKeyboardButton();
        USD_button.setText( "USD" );
        USD_button.setCallbackData( ServiceCommands.USD.toString() );

        InlineKeyboardButton BTC_button = new InlineKeyboardButton();
        BTC_button.setText( "BTC" );
        BTC_button.setCallbackData( ServiceCommands.BTC.toString() );

        rowInline.add( RUB_button );
        rowInline.add( USD_button );
        rowInline.add( BTC_button );

        rowsInline.add( rowInline );

        markup.setKeyboard( rowsInline );
        sendMessage.setReplyMarkup( markup );

        User user = userService.get( update );
        user.setCommands( ServiceCommands.CURRENCY );
        log.debug( "User " + user.getId() + " setCommands " + ServiceCommands.CURRENCY );

        return sendMessage;
    }

    public SendMessage createNewOrder(ServiceCommands currency, Update update) {
        User user = userService.get( update );
        Order newOrder = new Order();
        newOrder.setMoney( currency.toString() );
        user.setOrder( newOrder );
        user.setCommands( currency );
        SendMessage sendMessage = new SendMessage();
        Long chatId = update.getCallbackQuery().getFrom().getId();
        sendMessage.setChatId( String.valueOf( chatId ) );
        sendMessage.setText( "Выбрана валюта " + currency + " добавьте сумму вхождения" );
        log.debug( "User " + user.getId() + " setCommands " + currency );

        return sendMessage;
    }

    public SendMessage innerSum(Update update) {
        log.debug( "set Inner Sum " );
        Long chatId = update.getMessage().getFrom().getId();
        double innerSum = Double.parseDouble( update.getMessage().getText() );
        User user = userService.get( update );
        Order order = user.getOrder();
        order.setInnerSum( innerSum );


        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId( String.valueOf( chatId ) );
        sendMessage.setText( "Сумма вхождения " + innerSum + " теперь введите процент риска от 0 до 100" );
        return sendMessage;
    }

    public SendMessage setRisk(Update update) {

        log.debug( "set Risk " );
        Long chatId = update.getMessage().getFrom().getId();
        double risk = Double.parseDouble( update.getMessage().getText() );
        User user = userService.get( update );
        Order order = user.getOrder();
        order.setRisk( risk );

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId( String.valueOf( chatId ) );
        sendMessage.setText( "Процент риска установлен " + risk + " теперь введите ваш текущий баланс" );
        return sendMessage;

    }

    public SendMessage setBalance(Update update) {
        log.debug( "set Balance " );
        Long chatId = update.getMessage().getFrom().getId();
        double balance = Double.parseDouble( update.getMessage().getText() );
        User user = userService.get( update );
        Order order = user.getOrder();
        order.setBalance( balance );

        String answer = "Ваши данные \n" +
                "Сумма вхождения        : " + order.getInnerSum() + "\n" +
                "Процент риска          : " + order.getRisk() + "\n" +
                "Ваш баланс             : " + order.getBalance() + "\n" +
                "Результат расчета      : " + order.getCalc() + "\n";

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId( String.valueOf( chatId ) );
        sendMessage.setText( answer );
        return sendMessage;
    }

    public SendMessage timeoutMessage(Update update) {
        Long chatId = getChatId( update );
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId( String.valueOf( chatId ) );
        sendMessage.setText( "Время сессии истекло начните с команды /start" );

        return sendMessage;
    }

    private Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else {
            return update.getCallbackQuery().getMessage().getChatId();
        }
    }

    private SendMessage startMenu(Update update, SendMessage sendMessage) {
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
        log.debug( "Start Menu view - ok" );
        User user = userService.get( update );
        user.setCommands( ServiceCommands.START );
        log.debug( "User " + user.getId() + " setCommands " + ServiceCommands.START );
        return sendMessage;
    }
}
