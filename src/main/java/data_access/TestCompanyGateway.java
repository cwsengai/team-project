package data_access;
import api.Api;
import entity.Company;

public class TestCompanyGateway {
    public static void main(String[] args) {

        Api api = new Api("demo");
        AlphaVantageCompanyGateway gateway = new AlphaVantageCompanyGateway(api);

        Company c = gateway.fetchOverview("IBM");

        if (c == null) {
            System.out.println("Gateway returned null — maybe API limit or bad response.");
            return;
        }

        System.out.println("Symbol: " + c.getSymbol());
        System.out.println("Name: " + c.getName());
        System.out.println("Sector: " + c.getSector());
        System.out.println("Market Cap: " + c.getMarketCapitalization());
        System.out.println("Country: " + c.getCountry());
        System.out.println("EPS: " + c.getEPS());
    }
}

