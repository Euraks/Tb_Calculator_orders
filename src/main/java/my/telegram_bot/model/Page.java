package my.telegram_bot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Page {
    private int page;
    private int per_page;
    private int total;
    private int total_pages;
    @JsonProperty("data")
    private List<People> data;
}
