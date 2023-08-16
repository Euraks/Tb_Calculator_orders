package my.telegram_bot.service.enums;

public enum ServiceCommands {
    HELP( "О боте " ),
    START( "Старт " ),
    CURRENCY( "Выбор валюты " ),
    INNER_SUM( "Сумма вхождения " ),
    RISK( "Процент риска " ),
    BALANCE( "Баланс " );

    private final String value;

    ServiceCommands(String value) {
        this.value = value;
    }

    public boolean equals(String value) {
        return this.value.toString().equals( value );
    }

}
