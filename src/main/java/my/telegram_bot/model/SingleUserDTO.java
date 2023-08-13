package my.telegram_bot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SingleUserDTO {
    @JsonProperty("data")
    private People people;
    @JsonProperty("support")
    private Support support;
}
