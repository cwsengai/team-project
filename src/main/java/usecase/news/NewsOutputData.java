package usecase.news;

import java.util.List;

public class NewsOutputData {
    private final String symbol;
    private final List<String> statements;

    public NewsOutputData(String symbol, List<String> statements) {
        this.symbol = symbol;
        this.statements = statements;
    }

    public String getSymbol() {
        return symbol;
    }

    public List<String> getStatements() {
        return statements;
    }
}
