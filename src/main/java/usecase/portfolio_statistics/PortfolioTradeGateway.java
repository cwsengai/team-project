package usecase.portfolio_statistics;

import entity.SimulatedTradeRecord;

public interface PortfolioTradeGateway {
    java.util.List<SimulatedTradeRecord> fetchTradesForUser(java.util.UUID userId);
}
