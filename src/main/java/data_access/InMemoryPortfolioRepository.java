package data_access;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import entity.Portfolio;

/**
 * In-memory implementation of PortfolioRepository for testing and development.
 */
public class InMemoryPortfolioRepository implements PortfolioRepository {
    private final Map<String, Portfolio> portfolios;

    public InMemoryPortfolioRepository() {
        this.portfolios = new HashMap<>();
    }

    @Override
    public Optional<Portfolio> findById(String id) {
        return Optional.ofNullable(portfolios.get(id));
    }

    @Override
    public List<Portfolio> findByUserId(String userId) {
        return portfolios.values().stream()
                .filter(p -> p.getUserId() != null && p.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        portfolios.put(portfolio.getId(), portfolio);
        return portfolio;
    }

    @Override
    public void updateCash(String id, double cash) {
        Portfolio portfolio = portfolios.get(id);
        if (portfolio != null) {
            portfolio.setCurrentCash(cash);
        }
    }

    @Override
    public void delete(String id) {
        portfolios.remove(id);
    }

    /**
     * Clear all portfolios (useful for testing).
     */
    public void clear() {
        portfolios.clear();
    }
}
