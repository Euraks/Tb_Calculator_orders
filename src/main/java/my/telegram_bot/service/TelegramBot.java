package my.telegram_bot.service;

import lombok.extern.log4j.Log4j;
import my.telegram_bot.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Log4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final UpdateController updateController;

    @PostConstruct
    public void init() {
        updateController.registerBot( this );
    }

    public TelegramBot(BotConfig config, UpdateController updateController) {
        this.config = config;
        this.updateController = updateController;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate( update );
    }


    public void sendAnswerMessage(SendMessage sendMessage) {
        if (sendMessage != null) {
            try{
                execute( sendMessage );
            } catch(TelegramApiException e){
                log.error( e );
            }
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
