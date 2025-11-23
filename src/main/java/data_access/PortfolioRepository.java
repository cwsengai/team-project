//package data_access;
//
//import java.util.List;
//import java.util.Optional;
//
//import entity.Portfolio;
//
///**
// * Repository interface for Portfolio persistence.
// * Manages portfolio CRUD operations.
// */
//public interface PortfolioRepository {
//    /**
//     * Find a portfolio by its unique ID.
//     *
//     * @param id the portfolio ID (UUID)
//     * @return Optional containing the portfolio if found, empty otherwise
//     */
//    Optional<Portfolio> findById(String id);
//
//    /**
//     * Find all portfolios owned by a user.
//     *
//     * @param userId the user ID
//     * @return list of portfolios owned by the user
//     */
//    List<Portfolio> findByUserId(String userId);
//
//    /**
//     * Save or update a portfolio.
//     *
//     * @param portfolio the portfolio to save
//     * @return the saved portfolio with generated ID if new
//     */
//    Portfolio save(Portfolio portfolio);
//
//    /**
//     * Update the current cash balance for a portfolio.
//     *
//     * @param id the portfolio ID
//     * @param cash the new cash balance
//     */
//    void updateCash(String id, double cash);
//
//    /**
//     * Delete a portfolio and all its positions/trades.
//     *
//     * @param id the portfolio ID
//     */
//    void delete(String id);
//}
