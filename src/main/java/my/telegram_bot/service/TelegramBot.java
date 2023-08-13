package my.telegram_bot.service;

import my.telegram_bot.config.BotConfig;
import my.telegram_bot.model.People;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Objects;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private PeopleService service;

    private final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if ("/start".equals( messageText )) {
                startCommandReceived( chatId, update.getMessage().getChat().getUserName() );
            }
            if (messageText.contains( "/get_user" ) && (!messageText.contains( "/get_users" ))) {
                handlingCommandGetUser( messageText, chatId );
            }
            if (messageText.contains( "/get_users" )) {
                handlingCommandGetUsers( messageText, chatId );
            }
            if (messageText.contains( "/create_user" )) {
                handlingCommandCreateUser( messageText, chatId );
            }
        }
    }

    private void handlingCommandCreateUser(String messageText, long chatId) {
        if (messageText.length() == 12) {
            sendMessage( chatId, "Неправильный формат сообщения ? /create_user FIRST_NAME LAST_NAME {через пробел} ?" );
        } else {
            String[] data = messageText.split( " " );
            service.createUser( data[1] + " " + data[2] );
        }
    }


    private void handlingCommandGetUsers(String messageText, long chatId) {
        if (messageText.length() == 10) {
            getPage( chatId, 1 );
        } else {
            String num = messageText.substring( messageText.lastIndexOf( " " ) + 1 );
            getPage( chatId, Integer.parseInt( num ) );
        }
    }

    private void handlingCommandGetUser(String messageText, long chatId) {
        if (messageText.length() == 9) {
            sendMessage( chatId, " Неправильный формат сообщения ? /get_user ID {ID нужного пользователя} ?" );
        } else {
            String peopleId = messageText.substring( messageText.lastIndexOf( " " ) + 1 );
            People people = getPeopleForId( peopleId );
            sendMessage( chatId, people.toString() );
        }
    }

    private People getPeopleForId(String peopleId) {
        return service.getPeopleForId( peopleId );
    }

    private void getPage(long chatId, int numPage) {
        List<People> list = service.getPeople( numPage );
        for (People p : list) {
            sendMessage( chatId, String.valueOf( p.getId() ) );
            sendMessage( chatId, Objects.requireNonNullElse( p.getFirstName(), "No First Name" ) );
            sendMessage( chatId, Objects.requireNonNullElse( p.getLastName(), "No Last Name" ) );
            sendMessage( chatId, Objects.requireNonNullElse( p.getEmail(), "No Email" ) );
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi " + name;
        sendMessage( chatId, answer );
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
