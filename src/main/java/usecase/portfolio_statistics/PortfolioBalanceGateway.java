package usecase.portfolio_statistics;

import java.util.UUID;

/**
 * Gateway interface for retrieving portfolio balance information.
 * Provides access to a user's initial account balance.
 */
public interface PortfolioBalanceGateway {

    /**
     * Returns the initial balance of the user with the given ID.
     *
     * @param userId the unique identifier of the user
     * @return the user's initial account balance
     */
    double getInitialBalance(UUID userId);
}
