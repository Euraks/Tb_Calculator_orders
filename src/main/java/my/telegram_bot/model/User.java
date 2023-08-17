package my.telegram_bot.model;

import lombok.Data;
import my.telegram_bot.service.enums.ServiceCommands;

import java.time.LocalTime;

@Data
public class User {

    private Long id;

    private String firstName;

    private Boolean isBot;

    private String lastName;

    private String userName;

    private String languageCode;

    private Boolean canJoinGroups;

    private Boolean canReadAllGroupMessages;

    private Boolean supportInlineQueries;

    private ServiceCommands commands = ServiceCommands.START;

    private Order order;

    private LocalTime timeLastCommands ;

    public void setCommands(ServiceCommands commands) {
        timeLastCommands = LocalTime.now();
        this.commands = commands;
    }
}
