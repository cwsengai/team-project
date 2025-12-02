package app.gateway;

import usecase.portfolio_statistics.PortfolioBalanceGateway;

public class SupabasePortfolioGatewayAdapter implements PortfolioBalanceGateway {

    private final dataaccess.SupabasePortfolioDataAccessObject dao;

    public SupabasePortfolioGatewayAdapter() {
        this.dao = new dataaccess.SupabasePortfolioDataAccessObject();
    }

    @Override
    public double getInitialBalance(java.util.UUID userId) {
        return dao.getInitialBalance(userId);
    }
}
