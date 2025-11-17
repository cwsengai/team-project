# Database Quick Reference

## Connection String

```
jdbc:postgresql://localhost:5432/portfolio_tracker
```

## Quick Setup Commands

### 1. Create Database and User

```sql
-- Run as postgres superuser
CREATE DATABASE portfolio_tracker;
CREATE USER portfolio_user WITH ENCRYPTED PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE portfolio_tracker TO portfolio_user;

-- Connect to the database
\c portfolio_tracker

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO portfolio_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO portfolio_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO portfolio_user;
```

### 2. Run Schema

```bash
psql -U portfolio_user -d portfolio_tracker -f schema.sql
```

### 3. Verify Tables

```sql
\dt  -- List all tables
SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';
```

## Essential Queries

### Get User's Portfolios

```sql
SELECT p.*,
       (SELECT COUNT(*) FROM portfolio_positions pp WHERE pp.portfolio_id = p.id) as position_count
FROM portfolios p
WHERE p.user_id = 'user-uuid-here';
```

### Get Portfolio Positions with Current Prices

```sql
SELECT
    pp.id,
    c.ticker,
    c.name,
    pp.quantity,
    pp.avg_price,
    pp.quantity * pp.avg_price as cost_basis,
    (SELECT close FROM price_points
     WHERE company_id = c.id
       AND interval = 'DAILY'
     ORDER BY timestamp DESC
     LIMIT 1) as current_price,
    pp.unrealized_pl,
    pp.realized_pl
FROM portfolio_positions pp
JOIN companies c ON pp.company_id = c.id
WHERE pp.portfolio_id = 'portfolio-uuid-here'
  AND pp.quantity > 0;
```

### Get Trade History

```sql
SELECT
    t.executed_at,
    c.ticker,
    t.trade_type,
    t.quantity,
    t.price,
    t.quantity * t.price as total_value,
    t.fees
FROM trades t
JOIN companies c ON t.company_id = c.id
WHERE t.portfolio_id = 'portfolio-uuid-here'
ORDER BY t.executed_at DESC
LIMIT 50;
```

### Get Latest Prices for Multiple Tickers

```sql
SELECT DISTINCT ON (c.ticker)
    c.ticker,
    pp.close as price,
    pp.timestamp
FROM price_points pp
JOIN companies c ON pp.company_id = c.id
WHERE c.ticker = ANY(ARRAY['AAPL', 'GOOGL', 'MSFT'])
  AND pp.interval = 'DAILY'
ORDER BY c.ticker, pp.timestamp DESC;
```

### Calculate Portfolio Performance

```sql
SELECT
    p.id,
    p.name,
    p.current_cash,
    COALESCE(SUM(pp.quantity * pp.avg_price), 0) as total_invested,
    COALESCE(SUM(pp.unrealized_pl), 0) as unrealized_pl,
    COALESCE(SUM(pp.realized_pl), 0) as realized_pl,
    p.current_cash + COALESCE(SUM(pp.quantity * pp.avg_price), 0) + COALESCE(SUM(pp.unrealized_pl), 0) as current_value
FROM portfolios p
LEFT JOIN portfolio_positions pp ON p.id = pp.portfolio_id
WHERE p.id = 'portfolio-uuid-here'
GROUP BY p.id, p.name, p.current_cash;
```

## Sample Data

### Insert Test User

```sql
INSERT INTO users (email, password_hash, display_name)
VALUES ('test@example.com', 'hashed_password_here', 'Test User')
RETURNING id;
```

### Insert Test Companies

```sql
INSERT INTO companies (ticker, name, sector, industry, exchange) VALUES
('AAPL', 'Apple Inc.', 'Technology', 'Consumer Electronics', 'NASDAQ'),
('GOOGL', 'Alphabet Inc.', 'Technology', 'Internet Services', 'NASDAQ'),
('MSFT', 'Microsoft Corporation', 'Technology', 'Software', 'NASDAQ'),
('TSLA', 'Tesla, Inc.', 'Automotive', 'Electric Vehicles', 'NASDAQ'),
('AMZN', 'Amazon.com Inc.', 'Consumer Cyclical', 'E-commerce', 'NASDAQ');
```

### Insert Sample Price Data

```sql
-- First, get company IDs
WITH company_ids AS (
  SELECT id, ticker FROM companies WHERE ticker IN ('AAPL', 'GOOGL', 'MSFT')
)
INSERT INTO price_points (company_id, timestamp, interval, open, high, low, close, volume, source)
SELECT
  id,
  NOW() - INTERVAL '1 day',
  'DAILY',
  175.00,
  178.50,
  174.25,
  177.00,
  45000000,
  'AlphaVantage'
FROM company_ids WHERE ticker = 'AAPL';
```

### Create Test Portfolio

```sql
-- Get user ID first
WITH user_info AS (
  SELECT id FROM users WHERE email = 'test@example.com'
)
INSERT INTO portfolios (user_id, name, is_simulation, initial_cash, current_cash)
SELECT id, 'My First Portfolio', true, 10000.00, 7000.00
FROM user_info
RETURNING id;
```

## Maintenance Queries

### Clean Up Old Price Data (keep last 2 years)

```sql
DELETE FROM price_points
WHERE timestamp < NOW() - INTERVAL '2 years'
  AND interval = 'DAILY';
```

### Revoke Expired Sessions

```sql
UPDATE sessions
SET revoked_at = NOW()
WHERE expires_at < NOW()
  AND revoked_at IS NULL;
```

### Recalculate Position P/L

```sql
-- This would typically be done in application code
-- but here's the SQL logic
UPDATE portfolio_positions pp
SET unrealized_pl = (
  SELECT
    (latest.close - pp.avg_price) * pp.quantity
  FROM (
    SELECT DISTINCT ON (company_id)
      company_id, close
    FROM price_points
    WHERE interval = 'DAILY'
    ORDER BY company_id, timestamp DESC
  ) latest
  WHERE latest.company_id = pp.company_id
);
```

## Useful Views Already Created

### portfolio_summary

```sql
SELECT * FROM portfolio_summary WHERE user_id = 'user-uuid';
```

### position_details

```sql
SELECT * FROM position_details WHERE portfolio_id = 'portfolio-uuid';
```

### trade_history

```sql
SELECT * FROM trade_history WHERE portfolio_id = 'portfolio-uuid' LIMIT 20;
```

## Performance Tips

1. **Indexes are created** on frequently queried columns
2. **Use EXPLAIN ANALYZE** to check query performance
3. **Partition price_points** table by date if data grows large
4. **Archive old trades** to separate table if needed
5. **Cache latest prices** in application layer (15-minute TTL)

## Backup & Restore

### Backup

```bash
pg_dump -U portfolio_user portfolio_tracker > backup.sql
```

### Restore

```bash
psql -U portfolio_user portfolio_tracker < backup.sql
```
