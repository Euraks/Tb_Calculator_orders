package my.telegram_bot.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
public class Command {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    private Long userId;

    private String value;

    public Command(Integer id,  Long userId, String value) {
        this.id = id;
        this.userId = userId;
        this.value = value;
    }
}
