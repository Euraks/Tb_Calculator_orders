package my.telegram_bot.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Order {
    private String money;
    private double risk;
    private double amount;
    private double balance;

    public double getCalc() {
        return (balance / 10) * (risk / 100) + amount;
    }
}
