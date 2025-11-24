package use_case;

import entity.MarketIndex;
import java.util.List;

/**
 * Gateway interface for fetching market indices data.
 */
public interface MarketIndexGateway {
    /**
     * Fetch all major market indices (S&P 500, NASDAQ, Dow Jones).
     * @return List of market indices
     */
    List<MarketIndex> getMarketIndices() throws Exception;

    /**
     * Fetch a specific market index by symbol.
     * @param symbol The index symbol (e.g., "SPY", "QQQ", "DIA")
     * @return Market index data
     */
    MarketIndex getMarketIndex(String symbol) throws Exception;
}