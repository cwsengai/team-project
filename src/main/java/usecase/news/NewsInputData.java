package usecase.news;

public class NewsInputData {
    private final String symbol;

    public NewsInputData(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
