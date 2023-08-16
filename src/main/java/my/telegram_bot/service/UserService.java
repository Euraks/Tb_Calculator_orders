package my.telegram_bot.service;

import lombok.extern.log4j.Log4j;
import my.telegram_bot.model.User;
import my.telegram_bot.service.enums.ServiceCommands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Log4j
@Component
public class UserService {
    private final Map<Long, User> userMap = new HashMap<>();

    public User get(Update update) {
        Long userId = getId( update );
        if (userMap.containsKey( userId )) {
            User user = userMap.get( userId );
            if (user.getTimeLastCommands().isAfter( LocalTime.now().plusMinutes( 10 ) )){
                user.setCommands( ServiceCommands.START );
                userMap.put( userId,user );
            }
            return userMap.get( userId );
        } else {
            User newUser = new User();
            newUser.setCommands( ServiceCommands.START );
            userMap.put( userId,newUser );
            return newUser;
        }
    }

    private Long getId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        } else {
            return update.getCallbackQuery().getFrom().getId();
        }
    }
}
