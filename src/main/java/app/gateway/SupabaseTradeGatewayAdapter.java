package app.gateway;

import entity.SimulatedTradeRecord;
import usecase.portfolio_statistics.PortfolioTradeGateway;

public class SupabaseTradeGatewayAdapter implements PortfolioTradeGateway {

    private final dataaccess.SupabaseTradeDataAccessObject dao;

    public SupabaseTradeGatewayAdapter() {
        this.dao = new dataaccess.SupabaseTradeDataAccessObject();
    }

    @Override
    public java.util.List<SimulatedTradeRecord> fetchTradesForUser(java.util.UUID userId) {
        return dao.fetchTradesForUser(userId);
    }
}
