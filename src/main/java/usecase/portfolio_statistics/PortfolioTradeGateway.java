package usecase.portfolio_statistics;

import java.util.List;
import java.util.UUID;

import entity.SimulatedTradeRecord;

/**
 * Gateway interface for retrieving simulated trade records for a given user.
 * Provides access to the user's historical simulated trades.
 */
public interface PortfolioTradeGateway {

    /**
     * Fetches all simulated trades associated with the specified user.
     *
     * @param userId the unique identifier of the user
     * @return a list of the user's simulated trade records
     */
    List<SimulatedTradeRecord> fetchTradesForUser(UUID userId);
}
