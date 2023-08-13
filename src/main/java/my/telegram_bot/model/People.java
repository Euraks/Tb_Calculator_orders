package my.telegram_bot.model;

import lombok.Data;

@Data
public class People {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;
}
