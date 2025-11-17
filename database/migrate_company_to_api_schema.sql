-- Migration script to convert Company table from database schema to API schema
-- WARNING: This is a DESTRUCTIVE update that will modify the table structure
-- Make sure to backup your data before running this script

-- Step 1: Add new columns for API schema
ALTER TABLE companies 
ADD COLUMN IF NOT EXISTS symbol VARCHAR(10),
ADD COLUMN IF NOT EXISTS country VARCHAR(100),
ADD COLUMN IF NOT EXISTS eps FLOAT,
ADD COLUMN IF NOT EXISTS pe_ratio FLOAT,
ADD COLUMN IF NOT EXISTS dividend_per_share FLOAT,
ADD COLUMN IF NOT EXISTS dividend_yield FLOAT,
ADD COLUMN IF NOT EXISTS beta FLOAT;

-- Step 2: Migrate existing data
-- Copy ticker to symbol (primary identifier in API)
UPDATE companies SET symbol = ticker WHERE symbol IS NULL;

-- Set default values for new financial metrics
UPDATE companies SET 
    country = 'USA' WHERE country IS NULL,
    eps = 0.0 WHERE eps IS NULL,
    pe_ratio = 0.0 WHERE pe_ratio IS NULL,
    dividend_per_share = 0.0 WHERE dividend_per_share IS NULL,
    dividend_yield = 0.0 WHERE dividend_yield IS NULL,
    beta = 1.0 WHERE beta IS NULL;

-- Step 3: Drop database-specific columns that API doesn't use
ALTER TABLE companies DROP COLUMN IF EXISTS exchange;
ALTER TABLE companies DROP COLUMN IF EXISTS created_at;

-- Step 4: Rename market_cap to marketCapitalization for API compatibility
ALTER TABLE companies RENAME COLUMN market_cap TO market_capitalization;

-- Step 5: Make symbol NOT NULL and add unique constraint
ALTER TABLE companies ALTER COLUMN symbol SET NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS idx_companies_symbol ON companies(symbol);

-- Step 6: Update any foreign key references from ticker to symbol
-- (Add specific FK updates based on your schema)

COMMIT;
