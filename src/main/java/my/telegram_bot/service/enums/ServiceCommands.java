package my.telegram_bot.service.enums;


public enum ServiceCommands {
    HELP( "/info " ),
    START( "/start " ),
    CURRENCY( "/curenncy " ),
    INNER_SUM( "Сумма вхождения " ),
    RISK( "Процент риска " ),
    BALANCE( "Баланс " ),
    TIMEOUT("Время сессии закончилось"),
    RUB( "RUB" ),
    USD( "USD" ),
    BTC( "BTC" );

    private final String value;

    ServiceCommands(String value) {
        this.value = value;
    }

    public boolean equals(String value) {
        return this.value.compareTo( value )>0;
    }

    public String getValue() {
        return value;
    }
}
