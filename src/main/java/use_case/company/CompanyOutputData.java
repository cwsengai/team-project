package use_case.company;

public class CompanyOutputData {
    private final String symbol;
    private final String name;
    private final String sector;
    private final String industry;
    private final String description;

    public CompanyOutputData(String symbol, String name, String sector, String industry, String description) {
        this.symbol = symbol;
        this.name = name;
        this.sector = sector;
        this.industry = industry;
        this.description = description;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getSector() {
        return sector;
    }

    public String getIndustry() {
        return industry;
    }

    public String getDescription() {
        return description;
    }
}
