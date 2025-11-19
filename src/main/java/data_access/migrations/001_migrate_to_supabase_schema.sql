-- Migration: Update PostgreSQL schema to match Supabase schema
-- This migration updates the database to use 'symbol' instead of 'ticker' as primary key
-- and updates related foreign key references

-- Step 1: Rename ticker column to symbol in companies table
ALTER TABLE companies 
  RENAME COLUMN ticker TO symbol;

-- Step 2: Update portfolio_positions to use instrument_symbol instead of company_id
-- First, add the new column
ALTER TABLE portfolio_positions 
  ADD COLUMN IF NOT EXISTS instrument_symbol TEXT,
  ADD COLUMN IF NOT EXISTS instrument_type TEXT DEFAULT 'stock';

-- Populate instrument_symbol from companies.symbol via company_id
UPDATE portfolio_positions pp
SET instrument_symbol = c.symbol
FROM companies c
WHERE pp.company_id = c.id;

-- Make instrument_symbol NOT NULL after populating
ALTER TABLE portfolio_positions
  ALTER COLUMN instrument_symbol SET NOT NULL;

-- Drop the old company_id foreign key constraint (if exists)
ALTER TABLE portfolio_positions
  DROP CONSTRAINT IF EXISTS portfolio_positions_company_id_fkey;

-- Drop the company_id column
ALTER TABLE portfolio_positions
  DROP COLUMN IF EXISTS company_id;

-- Step 3: Update trades table to use instrument_symbol
-- Add new columns
ALTER TABLE trades
  ADD COLUMN IF NOT EXISTS instrument_symbol TEXT,
  ADD COLUMN IF NOT EXISTS instrument_type TEXT DEFAULT 'stock';

-- Populate from existing ticker column if it exists
UPDATE trades
SET instrument_symbol = ticker
WHERE ticker IS NOT NULL;

-- Or populate from company_id if that's what you have
-- UPDATE trades t
-- SET instrument_symbol = c.symbol
-- FROM companies c
-- WHERE t.company_id = c.id;

-- Make instrument_symbol NOT NULL after populating
ALTER TABLE trades
  ALTER COLUMN instrument_symbol SET NOT NULL;

-- Drop old columns (uncomment if you have these)
-- ALTER TABLE trades DROP COLUMN IF EXISTS ticker;
-- ALTER TABLE trades DROP COLUMN IF EXISTS company_id;

-- Step 4: Update price_points table to use company_symbol
-- Add new column
ALTER TABLE price_points
  ADD COLUMN IF NOT EXISTS company_symbol TEXT;

-- Populate company_symbol from companies.symbol via company_id
UPDATE price_points pp
SET company_symbol = c.symbol
FROM companies c
WHERE pp.company_id = c.id;

-- Make company_symbol NOT NULL after populating
ALTER TABLE price_points
  ALTER COLUMN company_symbol SET NOT NULL;

-- Drop the old company_id foreign key constraint
ALTER TABLE price_points
  DROP CONSTRAINT IF EXISTS price_points_company_id_fkey;

-- Drop the company_id column
ALTER TABLE price_points
  DROP COLUMN IF EXISTS company_id;

-- Drop the id column from price_points (Supabase schema doesn't have it)
ALTER TABLE price_points
  DROP COLUMN IF EXISTS id;

-- Step 5: Add exchange column to companies if it doesn't exist
ALTER TABLE companies
  ADD COLUMN IF NOT EXISTS exchange TEXT;

-- Step 6: Update unique constraints
-- Drop old unique constraint on portfolio_positions
ALTER TABLE portfolio_positions
  DROP CONSTRAINT IF EXISTS portfolio_positions_portfolio_id_company_id_key;

-- Add new unique constraint on portfolio_positions
ALTER TABLE portfolio_positions
  ADD CONSTRAINT portfolio_positions_portfolio_id_instrument_symbol_key 
  UNIQUE (portfolio_id, instrument_symbol);

-- Drop old unique constraint on price_points
ALTER TABLE price_points
  DROP CONSTRAINT IF EXISTS price_points_company_id_interval_timestamp_key;

-- Add new unique constraint on price_points
ALTER TABLE price_points
  ADD CONSTRAINT price_points_company_symbol_interval_timestamp_key
  UNIQUE (company_symbol, interval, timestamp);

-- Step 7: Update companies primary key (if needed)
-- Note: This is complex and may require recreating the table
-- For now, we'll keep the UUID id but symbol becomes the logical primary key

-- Create index on symbol for faster lookups
CREATE INDEX IF NOT EXISTS idx_companies_symbol ON companies(symbol);

-- Optional: Add comment to document the change
COMMENT ON COLUMN companies.symbol IS 'Primary identifier for companies (was ticker)';
COMMENT ON COLUMN portfolio_positions.instrument_symbol IS 'References companies.symbol (was company_id)';
COMMENT ON COLUMN trades.instrument_symbol IS 'References companies.symbol';
COMMENT ON COLUMN price_points.company_symbol IS 'References companies.symbol (was company_id)';
