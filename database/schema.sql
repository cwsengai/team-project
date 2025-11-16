-- Portfolio Tracker Database Schema
-- PostgreSQL with UUID support

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- AUTHENTICATION & USER MANAGEMENT
-- ============================================================================

-- 1) Users table
-- Purpose: Authentication & user profiles
CREATE TABLE users (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  email text NOT NULL UNIQUE,
  password_hash text NOT NULL,
  display_name text,
  created_at timestamptz DEFAULT now(),
  last_login timestamptz
);

CREATE INDEX idx_users_email ON users(email);

-- 2) Sessions table
-- Purpose: Store short-lived session tokens / refresh tokens
CREATE TABLE sessions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token text NOT NULL UNIQUE,
  expires_at timestamptz,
  created_at timestamptz DEFAULT now(),
  revoked_at timestamptz
);

CREATE INDEX idx_sessions_token ON sessions(token);
CREATE INDEX idx_sessions_user_id ON sessions(user_id);

-- ============================================================================
-- MARKET DATA
-- ============================================================================

-- 3) Companies table
-- Purpose: Company metadata for UI and joins
CREATE TABLE companies (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  ticker text NOT NULL UNIQUE,
  name text,
  sector text,
  industry text,
  exchange text,
  market_cap numeric,
  description text,
  metadata jsonb,
  created_at timestamptz DEFAULT now()
);

CREATE INDEX idx_companies_ticker ON companies(ticker);
CREATE INDEX idx_companies_sector ON companies(sector);

-- 4) Price points table
-- Purpose: Time series raw prices (high-frequency / exact points)
CREATE TABLE price_points (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id uuid NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
  timestamp timestamptz NOT NULL,
  interval text NOT NULL, -- 'DAILY','WEEKLY','MONTHLY','INTRADAY'
  open numeric,
  high numeric,
  low numeric,
  close numeric,
  volume numeric,
  source text,
  raw jsonb,
  UNIQUE(company_id, interval, timestamp)
);

CREATE INDEX idx_price_points_lookup ON price_points (company_id, interval, timestamp DESC);
CREATE INDEX idx_price_points_timestamp ON price_points (timestamp DESC);

-- 5) Candles table (optional)
-- Purpose: Aggregated OHLC candles for charting
CREATE TABLE candles (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id uuid NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
  interval text NOT NULL, -- 'DAILY','WEEKLY','MONTHLY','INTRADAY'
  start_time timestamptz NOT NULL,
  end_time timestamptz NOT NULL,
  open numeric NOT NULL,
  high numeric NOT NULL,
  low numeric NOT NULL,
  close numeric NOT NULL,
  volume numeric,
  created_at timestamptz DEFAULT now(),
  UNIQUE(company_id, interval, start_time)
);

CREATE INDEX idx_candles_lookup ON candles (company_id, interval, start_time DESC);

-- ============================================================================
-- PORTFOLIO MANAGEMENT
-- ============================================================================

-- 6) Portfolios table
-- Purpose: User portfolios (real or simulated)
CREATE TABLE portfolios (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  name text,
  is_simulation boolean DEFAULT true,
  initial_cash numeric DEFAULT 0,
  current_cash numeric DEFAULT 0,
  currency text DEFAULT 'USD',
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

CREATE INDEX idx_portfolios_user_id ON portfolios(user_id);
CREATE INDEX idx_portfolios_created ON portfolios(created_at DESC);

-- 7) Portfolio positions table
-- Purpose: Current holdings per portfolio (maps to Position.java)
CREATE TABLE portfolio_positions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  portfolio_id uuid NOT NULL REFERENCES portfolios(id) ON DELETE CASCADE,
  company_id uuid NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
  quantity numeric NOT NULL DEFAULT 0,
  avg_price numeric,
  realized_pl numeric DEFAULT 0,
  unrealized_pl numeric DEFAULT 0,
  last_updated timestamptz DEFAULT now(),
  UNIQUE(portfolio_id, company_id)
);

CREATE INDEX idx_positions_portfolio ON portfolio_positions(portfolio_id);
CREATE INDEX idx_positions_company ON portfolio_positions(company_id);

-- 8) Trades table
-- Purpose: Individual buy/sell events (maps to Trade.java)
CREATE TABLE trades (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  portfolio_id uuid NOT NULL REFERENCES portfolios(id) ON DELETE CASCADE,
  position_id uuid REFERENCES portfolio_positions(id) ON DELETE SET NULL,
  company_id uuid NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
  trade_type text NOT NULL CHECK (trade_type IN ('BUY', 'SELL')),
  quantity numeric NOT NULL CHECK (quantity > 0),
  price numeric NOT NULL CHECK (price >= 0),
  fees numeric DEFAULT 0 CHECK (fees >= 0),
  executed_at timestamptz NOT NULL,
  metadata jsonb,
  created_at timestamptz DEFAULT now()
);

CREATE INDEX idx_trades_portfolio ON trades(portfolio_id, executed_at DESC);
CREATE INDEX idx_trades_company ON trades(company_id, executed_at DESC);
CREATE INDEX idx_trades_position ON trades(position_id);

-- ============================================================================
-- AUDIT & HISTORY (Optional but recommended)
-- ============================================================================

-- 9) Portfolio snapshots table
-- Purpose: Track portfolio value over time for performance charts
CREATE TABLE portfolio_snapshots (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  portfolio_id uuid NOT NULL REFERENCES portfolios(id) ON DELETE CASCADE,
  total_value numeric NOT NULL,
  cash_value numeric NOT NULL,
  positions_value numeric NOT NULL,
  realized_gains numeric DEFAULT 0,
  unrealized_gains numeric DEFAULT 0,
  snapshot_time timestamptz NOT NULL,
  metadata jsonb,
  created_at timestamptz DEFAULT now()
);

CREATE INDEX idx_snapshots_portfolio ON portfolio_snapshots(portfolio_id, snapshot_time DESC);

-- ============================================================================
-- TRIGGERS & FUNCTIONS
-- ============================================================================

-- Auto-update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_portfolios_updated_at BEFORE UPDATE ON portfolios
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Auto-update last_updated on positions
CREATE TRIGGER update_positions_last_updated BEFORE UPDATE ON portfolio_positions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- VIEWS (Useful queries)
-- ============================================================================

-- Portfolio summary view
CREATE OR REPLACE VIEW portfolio_summary AS
SELECT 
    p.id as portfolio_id,
    p.user_id,
    p.name as portfolio_name,
    p.current_cash,
    p.currency,
    COUNT(pp.id) as position_count,
    COALESCE(SUM(pp.quantity * pp.avg_price), 0) as total_cost_basis,
    COALESCE(SUM(pp.unrealized_pl), 0) as total_unrealized_pl,
    COALESCE(SUM(pp.realized_pl), 0) as total_realized_pl,
    p.created_at,
    p.updated_at
FROM portfolios p
LEFT JOIN portfolio_positions pp ON p.id = pp.portfolio_id
GROUP BY p.id, p.user_id, p.name, p.current_cash, p.currency, p.created_at, p.updated_at;

-- Position details view
CREATE OR REPLACE VIEW position_details AS
SELECT 
    pp.id as position_id,
    pp.portfolio_id,
    c.ticker,
    c.name as company_name,
    pp.quantity,
    pp.avg_price,
    pp.quantity * pp.avg_price as cost_basis,
    pp.realized_pl,
    pp.unrealized_pl,
    pp.last_updated
FROM portfolio_positions pp
JOIN companies c ON pp.company_id = c.id
WHERE pp.quantity > 0;

-- Trade history view
CREATE OR REPLACE VIEW trade_history AS
SELECT 
    t.id as trade_id,
    t.portfolio_id,
    c.ticker,
    c.name as company_name,
    t.trade_type,
    t.quantity,
    t.price,
    t.quantity * t.price as trade_value,
    t.fees,
    t.executed_at,
    t.created_at
FROM trades t
JOIN companies c ON t.company_id = c.id
ORDER BY t.executed_at DESC;
