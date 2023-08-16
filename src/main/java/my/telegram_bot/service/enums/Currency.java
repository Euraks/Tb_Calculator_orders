package my.telegram_bot.service.enums;

public enum Currency {
    RUB( "RUB" ),
    USD( "USD" ),
    BTC( "BTC" );


    private final String value;

    Currency(String value) {
        this.value = value;
    }

    public boolean equals(String value) {
        return this.value.compareTo( value ) > 0;
    }

    public String getValue() {
        return value;
    }
}
