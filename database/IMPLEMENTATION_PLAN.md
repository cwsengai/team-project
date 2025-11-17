# Database Implementation Plan

## Overview

This document outlines the implementation strategy for integrating the PostgreSQL database with the existing Clean Architecture portfolio tracker system.

## Implementation Phases

### Phase 1: Foundation & User Management

**Goal**: Implement core authentication and user management

#### 1.1 Entity Updates

- [ ] Create `User` entity class

  - Map to `users` table
  - Fields: id, email, displayName, createdAt, lastLogin
  - No password field (keep in data layer only)

- [ ] Create `Session` entity class (if needed)
  - Map to `sessions` table
  - For managing user sessions

#### 1.2 Data Access Layer

- [ ] Create `UserRepository` interface

  ```java
  - User findById(String id)
  - User findByEmail(String email)
  - void save(User user)
  - void updateLastLogin(String userId)
  ```

- [ ] Create `UserRepositoryImpl` (PostgreSQL)

  - Implement JDBC/JPA access to `users` table
  - Password hashing utilities
  - Email validation

- [ ] Create `SessionRepository` interface
  ```java
  - Session create(String userId)
  - Session findByToken(String token)
  - void revoke(String token)
  - void cleanupExpired()
  ```

#### 1.3 Use Cases

- [ ] `RegisterUser` use case
- [ ] `LoginUser` use case
- [ ] `LogoutUser` use case
- [ ] `ValidateSession` use case

---

### Phase 2: Company/Ticker Management

**Goal**: Create company metadata and price data infrastructure

#### 2.1 Entity Updates

- [ ] Create `Company` entity class

  - Map to `companies` table
  - Fields: id, ticker, name, sector, industry, exchange, marketCap, description

- [ ] Update `PricePoint` entity

  - Add companyId field
  - Add interval field (enum: DAILY, WEEKLY, MONTHLY, INTRADAY)
  - Add open, high, low, volume fields
  - Map to `price_points` table

- [ ] Update `Candle` entity

  - Add all OHLC fields
  - Add interval and time range
  - Map to `candles` table

- [ ] Update `TimeInterval` enum
  - Values: DAILY, WEEKLY, MONTHLY, INTRADAY

#### 2.2 Data Access Layer

- [ ] Create `CompanyRepository` interface

  ```java
  - Company findByTicker(String ticker)
  - Company findById(String id)
  - List<Company> findBySector(String sector)
  - void save(Company company)
  - void saveAll(List<Company> companies)
  ```

- [ ] Create `PriceRepository` interface

  ```java
  - void savePricePoints(String ticker, List<PricePoint> points)
  - PricePoint getLatestPrice(String ticker)
  - Map<String, PricePoint> getLatestPrices(String[] tickers)
  - List<PricePoint> getHistoricalPrices(String ticker, LocalDate start, LocalDate end, TimeInterval interval)
  ```

- [ ] Update `StockDataGateway` implementation
  - Fetch from AlphaVantage API
  - Cache in `price_points` table
  - Return from cache if fresh (< 15 minutes for real-time)

#### 2.3 Use Cases

- [ ] `SyncCompanyData` use case (populate companies table)
- [ ] `FetchAndCachePrices` use case
- [ ] `GetHistoricalData` use case (for charts)

---

### Phase 3: Portfolio & Position Management

**Goal**: Migrate portfolio data to PostgreSQL

#### 3.1 Entity Updates

- [ ] Update `Portfolio` entity

  - Add userId field (foreign key to User)
  - Add name field
  - Add isSimulation flag
  - Add initialCash field
  - Add currentCash field (rename from cash)
  - Add currency field
  - Add createdAt, updatedAt timestamps

- [ ] Update `Position` entity

  - Add id field (UUID)
  - Add portfolioId field
  - Add companyId field (instead of just ticker)
  - Add realizedPL field
  - Add unrealizedPL field
  - Add lastUpdated timestamp
  - Keep ticker for convenience (denormalized)

- [ ] Update `Trade` entity
  - Add portfolioId field
  - Add positionId field
  - Add companyId field
  - Change isBuy to tradeType enum (BUY/SELL)
  - Add fees field
  - Rename timestamp to executedAt

#### 3.2 Data Access Layer

- [ ] Update `PortfolioRepository` interface

  ```java
  - Portfolio findById(String id)
  - List<Portfolio> findByUserId(String userId)
  - void save(Portfolio portfolio)
  - void updateCash(String portfolioId, double newCash)
  - void delete(String portfolioId)
  ```

- [ ] Create `PortfolioRepositoryPostgres` implementation

  - Replace `InMemoryPortfolioRepository`
  - Handle positions as separate table
  - Handle trades as separate table
  - Use transactions for consistency

- [ ] Create `PositionRepository` interface

  ```java
  - List<Position> findByPortfolioId(String portfolioId)
  - Position findByPortfolioAndCompany(String portfolioId, String companyId)
  - void save(Position position)
  - void updatePL(String positionId, double realized, double unrealized)
  ```

- [ ] Create `TradeRepository` interface
  ```java
  - void save(Trade trade)
  - List<Trade> findByPortfolioId(String portfolioId)
  - List<Trade> findByPosition(String positionId)
  - List<Trade> findByPortfolioAndDateRange(String portfolioId, LocalDate start, LocalDate end)
  ```

#### 3.3 Use Cases

- [ ] Update `TrackPortfolio` use case

  - Fetch positions from database
  - Calculate unrealized P/L with latest prices
  - Update positions table with new P/L values

- [ ] Create `ExecuteTrade` use case

  - Create new trade record
  - Update position (quantity, avg cost)
  - Update portfolio cash
  - Calculate realized P/L on sells
  - Use database transaction

- [ ] Create `GetTradeHistory` use case
- [ ] Create `CreatePortfolio` use case
- [ ] Create `DeletePortfolio` use case

---

### Phase 4: Advanced Features

**Goal**: Snapshots, performance tracking, analytics

#### 4.1 Data Access Layer

- [ ] Create `SnapshotRepository` interface
  ```java
  - void createSnapshot(String portfolioId)
  - List<Snapshot> getHistory(String portfolioId, LocalDate start, LocalDate end)
  - Snapshot getLatest(String portfolioId)
  ```

#### 4.2 Use Cases

- [ ] `CreatePortfolioSnapshot` use case (scheduled daily)
- [ ] `GetPortfolioPerformance` use case (charts)
- [ ] `CalculateReturns` use case (time-weighted returns)

---

## Database Connection Setup

### Required Dependencies (pom.xml)

```xml
<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
</dependency>

<!-- Connection Pooling -->
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.1.0</version>
</dependency>

<!-- Optional: JPA/Hibernate -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.4.4.Final</version>
</dependency>
```

### Configuration Files

#### `database.properties`

```properties
db.url=jdbc:postgresql://localhost:5432/portfolio_tracker
db.username=portfolio_user
db.password=your_secure_password
db.driver=org.postgresql.Driver
db.pool.size=10
```

#### Database Connection Manager

```java
package data_access;

public class DatabaseConnectionManager {
    private static HikariDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            // Load from properties
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            config.setMaximumPoolSize(10);
            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }
}
```

---

## Migration Strategy

### Option A: Big Bang (Not Recommended)

- Implement all phases at once
- High risk, difficult to test

### Option B: Incremental (Recommended)

1. **Week 1**: Phase 1 - User management
2. **Week 2**: Phase 2 - Company & price data
3. **Week 3**: Phase 3 - Portfolio migration
4. **Week 4**: Phase 4 - Advanced features

### Option C: Dual Write (Zero Downtime)

1. Keep `InMemoryRepository` working
2. Add database repositories
3. Write to both
4. Verify consistency
5. Switch reads to database
6. Remove in-memory implementation

---

## Testing Strategy

### Unit Tests

- Test each repository with H2 in-memory database
- Mock database connections
- Test SQL query generation

### Integration Tests

- Use Testcontainers with PostgreSQL
- Test full repository implementations
- Test transaction handling

### Migration Tests

- Test data migration scripts
- Verify data integrity
- Test rollback procedures

---

## Exposed Functions/API Design

### Repository Layer (Package: `data_access`)

#### UserRepository

```java
public interface UserRepository {
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    User save(User user);
    void updateLastLogin(String userId, LocalDateTime timestamp);
}
```

#### PortfolioRepository (Updated)

```java
public interface PortfolioRepository {
    Optional<Portfolio> findById(String id);
    List<Portfolio> findByUserId(String userId);
    Portfolio save(Portfolio portfolio);
    void delete(String id);
    void updateCash(String id, double cash);
}
```

#### PositionRepository

```java
public interface PositionRepository {
    List<Position> findByPortfolioId(String portfolioId);
    Optional<Position> findByPortfolioAndTicker(String portfolioId, String ticker);
    Position save(Position position);
    void updateUnrealizedPL(String positionId, double unrealizedPL);
}
```

#### TradeRepository

```java
public interface TradeRepository {
    Trade save(Trade trade);
    List<Trade> findByPortfolioId(String portfolioId);
    List<Trade> findByPositionId(String positionId);
    List<Trade> findByPortfolioInDateRange(String portfolioId, LocalDateTime start, LocalDateTime end);
}
```

#### CompanyRepository

```java
public interface CompanyRepository {
    Optional<Company> findByTicker(String ticker);
    Optional<Company> findById(String id);
    List<Company> findBySector(String sector);
    Company save(Company company);
    void saveAll(List<Company> companies);
}
```

#### PriceRepository

```java
public interface PriceRepository {
    void savePricePoint(PricePoint pricePoint);
    void savePricePoints(List<PricePoint> pricePoints);
    Optional<PricePoint> getLatestPrice(String ticker, TimeInterval interval);
    Map<String, PricePoint> getLatestPrices(List<String> tickers);
    List<PricePoint> getHistoricalPrices(String ticker, LocalDateTime start, LocalDateTime end, TimeInterval interval);
    void cleanup(LocalDateTime olderThan); // Delete old price data
}
```

---

## Next Steps

1. **Setup Database Environment**

   - Install PostgreSQL
   - Create database and user
   - Run schema.sql

2. **Add Dependencies**

   - Update pom.xml with PostgreSQL driver
   - Add connection pooling library

3. **Create Missing Entities**

   - User.java
   - Company.java
   - Update existing entities

4. **Implement First Repository**

   - Start with UserRepository
   - Write tests
   - Integrate with use case

5. **Iterate Through Phases**
   - Complete one phase at a time
   - Test thoroughly
   - Update documentation

---

## Clean Architecture Compliance

- ✅ **Entities**: Pure business logic, no database dependencies
- ✅ **Use Cases**: Depend on repository interfaces, not implementations
- ✅ **Interface Adapters**: Repository implementations in data_access package
- ✅ **Frameworks**: Database-specific code isolated in implementations
- ✅ **Dependency Rule**: Dependencies point inward (DB → Repositories → Use Cases → Entities)
