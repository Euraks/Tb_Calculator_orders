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
        sendMessage.setText( "Выбрана валюта RUB добавьте сумму вхождения" );
        log.debug( "User " + user.getId() + " setCommands " + currency );

        return sendMessage;
    }
}
