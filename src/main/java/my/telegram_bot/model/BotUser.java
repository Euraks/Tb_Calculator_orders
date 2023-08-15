package my.telegram_bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Data
@Entity
@NoArgsConstructor

public class BotUser {
    @Id
    @Column(name = "database_id", nullable = false)
    private Long id;

    private String firstName;

    private Boolean isBot;

    private String lastName;

    private String userName;

    private String languageCode;

    private Boolean canJoinGroups;

    private Boolean canReadAllGroupMessages;

    private Boolean supportInlineQueries;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Command> commandList = new LinkedList<>();

    public BotUser(Long id, String firstName, Boolean isBot, String lastName, String userName, String languageCode, Boolean canJoinGroups, Boolean canReadAllGroupMessages, Boolean supportInlineQueries) {
        this.id = id;
        this.firstName = firstName;
        this.isBot = isBot;
        this.lastName = lastName;
        this.userName = userName;
        this.languageCode = languageCode;
        this.canJoinGroups = canJoinGroups;
        this.canReadAllGroupMessages = canReadAllGroupMessages;
        this.supportInlineQueries = supportInlineQueries;
    }
}
