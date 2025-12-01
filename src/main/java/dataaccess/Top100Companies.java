package dataaccess;

import java.util.Arrays;
import java.util.List;

/**
 * Predefined list of top companies by market capitalization.
 * This represents the "Top 100 Fortune" companies.
 * Note: For production, this could come from a database or external API.
 * For now, we maintain a curated list of major public companies.
 */
public class Top100Companies {

    /**
     * List of ticker symbols for top companies.
     * Ordered approximately by market cap (largest first).
     */
    public static final List<String> TICKERS = Arrays.asList(
            // Top 20 - Mega Cap Tech & Finance
            "AAPL",
            // Apple
            "MSFT",
            // Microsoft
            "GOOGL",
            // Alphabet (Google)
            "AMZN",
            // Amazon
            "NVDA",
            // NVIDIA
            "META",
            // Meta (Facebook)
            "TSLA",
            // Tesla
            "BRK.B",
            // Berkshire Hathaway
            "V",
            // Visa
            "UNH",
            // UnitedHealth
            "JNJ",
            // Johnson & Johnson
            "WMT",
            // Walmart
            "JPM",
            // JPMorgan Chase
            "MA",
            // Mastercard
            "XOM",
            // Exxon Mobil
            "PG",
            // Procter & Gamble
            "HD",
            // Home Depot
            "CVX",
            // Chevron
            "AVGO",
            // Broadcom
            "MRK",
            // Merck

            // Top 21-40
            "ABBV",
            // AbbVie
            "PEP",
            // PepsiCo
            "KO",
            // Coca-Cola
            "COST",
            // Costco
            "ADBE",
            // Adobe
            "TMO",
            // Thermo Fisher
            "MCD",
            // McDonald's
            "CSCO",
            // Cisco
            "ACN",
            // Accenture
            "ABT",
            // Abbott Labs
            "NKE",
            // Nike
            "LLY",
            // Eli Lilly
            "TXN",
            // Texas Instruments
            "DHR",
            // Danaher
            "CRM",
            // Salesforce
            "NEE",
            // NextEra Energy
            "DIS",
            // Disney
            "VZ",
            // Verizon
            "CMCSA",
            // Comcast
            "ORCL",
            // Oracle

            // Top 41-60
            "INTC",
            // Intel
            "NFLX",
            // Netflix
            "AMD",
            // AMD
            "PFE",
            // Pfizer
            "PM",
            // Philip Morris
            "T",
            // AT&T
            "UPS",
            // UPS
            "BA",
            // Boeing
            "IBM",
            // IBM
            "QCOM",
            // Qualcomm
            "HON",
            // Honeywell
            "AMGN",
            // Amgen
            "RTX",
            // Raytheon
            "UNP",
            // Union Pacific
            "SPGI",
            // S&P Global
            "LOW",
            // Lowe's
            "CAT",
            // Caterpillar
            "SBUX",
            // Starbucks
            "GS",
            // Goldman Sachs
            "INTU",
            // Intuit

            // Top 61-80
            "AXP",
            // American Express
            "CVS",
            // CVS Health
            "DE",
            // Deere & Co
            "BLK",
            // BlackRock
            "MDLZ",
            // Mondelez
            "GILD",
            // Gilead Sciences
            "ADP",
            // ADP
            "MMM",
            // 3M
            "TJX",
            // TJX Companies
            "BKNG",
            // Booking Holdings
            "ISRG",
            // Intuitive Surgical
            "AMT",
            // American Tower
            "REGN",
            // Regeneron
            "CI",
            // Cigna
            "VRTX",
            // Vertex Pharma
            "CB",
            // Chubb
            "MO",
            // Altria
            "SYK",
            // Stryker
            "ZTS",
            // Zoetis
            "BDX",
            // Becton Dickinson

            // Top 81-100
            "TGT",
            // Target
            "SO",
            // Southern Company
            "USB",
            // US Bancorp
            "PLD",
            // Prologis
            "DUK",
            // Duke Energy
            "CME",
            // CME Group
            "CSX",
            // CSX Corp
            "CL",
            // Colgate-Palmolive
            "ITW",
            // Illinois Tool Works
            "NSC",
            // Norfolk Southern
            "APD",
            // Air Products
            "EOG",
            // EOG Resources
            "WM",
            // Waste Management
            "SHW",
            // Sherwin-Williams
            "MCO",
            // Moody's
            "CCI",
            // Crown Castle
            "EL",
            // Estée Lauder
            "SCHW",
            // Charles Schwab
            "AON",
            // Aon
            "HUM"
            // Humana
    );

    /**
     * Returns the full list of the top 100 company ticker symbols.
     *
     * @return an unmodifiable list containing all 100 ticker symbols
     */
    public static List<String> getAll() {
        return TICKERS;
    }

    /**
     * Returns a subset of the top company ticker symbols for testing purposes.
     * This is useful when avoiding API rate limits by reducing the number of
     * external requests.
     *
     * @param count the number of ticker symbols to return
     * @return a list containing the first {@code count} ticker symbols,
     *         or fewer if {@code count} exceeds the total list size
     */
    public static List<String> getSample(int count) {
        return TICKERS.subList(0, Math.min(count, TICKERS.size()));
    }
}
