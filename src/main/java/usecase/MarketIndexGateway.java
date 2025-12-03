package usecase;

import java.util.List;

import entity.MarketIndex;

/**
 * Gateway interface for fetching market index data.
 */
public interface MarketIndexGateway {

    /**
     * Fetches all major market indices.
     *
     * @return a list of market indices
     * @throws Exception if the data cannot be retrieved
     */
    List<MarketIndex> getMarketIndices() throws Exception;

    /**
     * Fetches data for a specific market index by symbol.
     *
     * @param symbol the index symbol (e.g., "SPY", "QQQ", "DIA")
     * @return the requested market index
     * @throws Exception if the index cannot be retrieved
     */
    MarketIndex getMarketIndex(String symbol) throws Exception;
}
