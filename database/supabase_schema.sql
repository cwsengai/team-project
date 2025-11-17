-- Portfolio Tracker Database Schema for Supabase
-- Integrates with Supabase Auth (auth.users table)

-- ============================================================================
-- USER PROFILES (extends Supabase auth.users)
-- ============================================================================

-- User profiles table (linked to auth.users)
CREATE TABLE IF NOT EXISTS public.user_profiles (
  id uuid PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  display_name text,
  created_at timestamptz DEFAULT now(),
  last_login timestamptz
);

-- Enable Row Level Security
ALTER TABLE public.user_profiles ENABLE ROW LEVEL SECURITY;

-- RLS Policies: Users can only read/update their own profile
CREATE POLICY "Users can view own profile" 
  ON public.user_profiles FOR SELECT 
  USING (auth.uid() = id);

CREATE POLICY "Users can update own profile" 
  ON public.user_profiles FOR UPDATE 
  USING (auth.uid() = id);

CREATE POLICY "Users can insert own profile" 
  ON public.user_profiles FOR INSERT 
  WITH CHECK (auth.uid() = id);

-- ============================================================================
-- MARKET DATA
-- ============================================================================

-- Companies table (public read, service role write)
CREATE TABLE IF NOT EXISTS public.companies (
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

CREATE INDEX IF NOT EXISTS idx_companies_ticker ON public.companies(ticker);
CREATE INDEX IF NOT EXISTS idx_companies_sector ON public.companies(sector);

ALTER TABLE public.companies ENABLE ROW LEVEL SECURITY;

-- RLS: Anyone can read companies
CREATE POLICY "Companies are publicly readable" 
  ON public.companies FOR SELECT 
  TO authenticated, anon 
  USING (true);

-- Price points table
CREATE TABLE IF NOT EXISTS public.price_points (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id uuid NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
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

CREATE INDEX IF NOT EXISTS idx_price_points_lookup ON public.price_points (company_id, interval, timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_price_points_timestamp ON public.price_points (timestamp DESC);

ALTER TABLE public.price_points ENABLE ROW LEVEL SECURITY;

-- RLS: Anyone can read price points
CREATE POLICY "Price points are publicly readable" 
  ON public.price_points FOR SELECT 
  TO authenticated, anon 
  USING (true);

-- Candles table (optional)
CREATE TABLE IF NOT EXISTS public.candles (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id uuid NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
  interval text NOT NULL,
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

CREATE INDEX IF NOT EXISTS idx_candles_lookup ON public.candles (company_id, interval, start_time DESC);

ALTER TABLE public.candles ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Candles are publicly readable" 
  ON public.candles FOR SELECT 
  TO authenticated, anon 
  USING (true);

-- ============================================================================
-- PORTFOLIO MANAGEMENT
-- ============================================================================

-- Portfolios table
CREATE TABLE IF NOT EXISTS public.portfolios (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  name text,
  is_simulation boolean DEFAULT true,
  initial_cash numeric DEFAULT 0,
  current_cash numeric DEFAULT 0,
  currency text DEFAULT 'USD',
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_portfolios_user_id ON public.portfolios(user_id);
CREATE INDEX IF NOT EXISTS idx_portfolios_created ON public.portfolios(created_at DESC);

ALTER TABLE public.portfolios ENABLE ROW LEVEL SECURITY;

-- RLS: Users can only access their own portfolios
CREATE POLICY "Users can view own portfolios" 
  ON public.portfolios FOR SELECT 
  USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own portfolios" 
  ON public.portfolios FOR INSERT 
  WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own portfolios" 
  ON public.portfolios FOR UPDATE 
  USING (auth.uid() = user_id);

CREATE POLICY "Users can delete own portfolios" 
  ON public.portfolios FOR DELETE 
  USING (auth.uid() = user_id);

-- Portfolio positions table
CREATE TABLE IF NOT EXISTS public.portfolio_positions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  portfolio_id uuid NOT NULL REFERENCES public.portfolios(id) ON DELETE CASCADE,
  company_id uuid NOT NULL REFERENCES public.companies(id) ON DELETE RESTRICT,
  quantity numeric NOT NULL DEFAULT 0,
  avg_price numeric,
  realized_pl numeric DEFAULT 0,
  unrealized_pl numeric DEFAULT 0,
  last_updated timestamptz DEFAULT now(),
  UNIQUE(portfolio_id, company_id)
);

CREATE INDEX IF NOT EXISTS idx_positions_portfolio ON public.portfolio_positions(portfolio_id);
CREATE INDEX IF NOT EXISTS idx_positions_company ON public.portfolio_positions(company_id);

ALTER TABLE public.portfolio_positions ENABLE ROW LEVEL SECURITY;

-- RLS: Users can access positions for their portfolios
CREATE POLICY "Users can view own positions" 
  ON public.portfolio_positions FOR SELECT 
  USING (
    portfolio_id IN (
      SELECT id FROM public.portfolios WHERE user_id = auth.uid()
    )
  );

CREATE POLICY "Users can insert own positions" 
  ON public.portfolio_positions FOR INSERT 
  WITH CHECK (
    portfolio_id IN (
      SELECT id FROM public.portfolios WHERE user_id = auth.uid()
    )
  );

CREATE POLICY "Users can update own positions" 
  ON public.portfolio_positions FOR UPDATE 
  USING (
    portfolio_id IN (
      SELECT id FROM public.portfolios WHERE user_id = auth.uid()
    )
  );

CREATE POLICY "Users can delete own positions" 
  ON public.portfolio_positions FOR DELETE 
  USING (
    portfolio_id IN (
      SELECT id FROM public.portfolios WHERE user_id = auth.uid()
    )
  );

-- Trades table
CREATE TABLE IF NOT EXISTS public.trades (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  portfolio_id uuid NOT NULL REFERENCES public.portfolios(id) ON DELETE CASCADE,
  position_id uuid REFERENCES public.portfolio_positions(id) ON DELETE SET NULL,
  company_id uuid NOT NULL REFERENCES public.companies(id) ON DELETE RESTRICT,
  trade_type text NOT NULL CHECK (trade_type IN ('BUY', 'SELL')),
  quantity numeric NOT NULL CHECK (quantity > 0),
  price numeric NOT NULL CHECK (price >= 0),
  fees numeric DEFAULT 0 CHECK (fees >= 0),
  executed_at timestamptz NOT NULL,
  metadata jsonb,
  created_at timestamptz DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_trades_portfolio ON public.trades(portfolio_id, executed_at DESC);
CREATE INDEX IF NOT EXISTS idx_trades_company ON public.trades(company_id, executed_at DESC);
CREATE INDEX IF NOT EXISTS idx_trades_position ON public.trades(position_id);

ALTER TABLE public.trades ENABLE ROW LEVEL SECURITY;

-- RLS: Users can access trades for their portfolios
CREATE POLICY "Users can view own trades" 
  ON public.trades FOR SELECT 
  USING (
    portfolio_id IN (
      SELECT id FROM public.portfolios WHERE user_id = auth.uid()
    )
  );

CREATE POLICY "Users can insert own trades" 
  ON public.trades FOR INSERT 
  WITH CHECK (
    portfolio_id IN (
      SELECT id FROM public.portfolios WHERE user_id = auth.uid()
    )
  );

-- ============================================================================
-- AUDIT & HISTORY
-- ============================================================================

-- Portfolio snapshots table
CREATE TABLE IF NOT EXISTS public.portfolio_snapshots (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  portfolio_id uuid NOT NULL REFERENCES public.portfolios(id) ON DELETE CASCADE,
  total_value numeric NOT NULL,
  cash_value numeric NOT NULL,
  positions_value numeric NOT NULL,
  realized_gains numeric DEFAULT 0,
  unrealized_gains numeric DEFAULT 0,
  snapshot_time timestamptz NOT NULL,
  metadata jsonb,
  created_at timestamptz DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_snapshots_portfolio ON public.portfolio_snapshots(portfolio_id, snapshot_time DESC);

ALTER TABLE public.portfolio_snapshots ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view own snapshots" 
  ON public.portfolio_snapshots FOR SELECT 
  USING (
    portfolio_id IN (
      SELECT id FROM public.portfolios WHERE user_id = auth.uid()
    )
  );

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

DROP TRIGGER IF EXISTS update_portfolios_updated_at ON public.portfolios;
CREATE TRIGGER update_portfolios_updated_at BEFORE UPDATE ON public.portfolios
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_positions_last_updated ON public.portfolio_positions;
CREATE TRIGGER update_positions_last_updated BEFORE UPDATE ON public.portfolio_positions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- VIEWS
-- ============================================================================

-- Portfolio summary view
CREATE OR REPLACE VIEW public.portfolio_summary AS
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
FROM public.portfolios p
LEFT JOIN public.portfolio_positions pp ON p.id = pp.portfolio_id
GROUP BY p.id, p.user_id, p.name, p.current_cash, p.currency, p.created_at, p.updated_at;

-- Position details view
CREATE OR REPLACE VIEW public.position_details AS
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
FROM public.portfolio_positions pp
JOIN public.companies c ON pp.company_id = c.id
WHERE pp.quantity > 0;

-- Trade history view
CREATE OR REPLACE VIEW public.trade_history AS
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
FROM public.trades t
JOIN public.companies c ON t.company_id = c.id
ORDER BY t.executed_at DESC;
