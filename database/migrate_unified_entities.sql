-- Migration script to create unified entity models
-- Merges database persistence needs with API business logic needs
-- WARNING: DESTRUCTIVE updates - backup data first

-- =============================================================================
-- POSITION TABLE - Unified Model
-- =============================================================================

-- Add API-expected columns while keeping database fields
ALTER TABLE positions
ADD COLUMN IF NOT EXISTS current_price DOUBLE PRECISION DEFAULT 0.0,
ADD COLUMN IF NOT EXISTS market_value DOUBLE PRECISION DEFAULT 0.0;

-- Rename columns for consistency
ALTER TABLE positions RENAME COLUMN realized_pl TO realized_gains;
ALTER TABLE positions RENAME COLUMN unrealized_pl TO unrealized_gains;

-- Update computed fields (market_value = quantity * current_price)
-- This will need to be updated by application logic when prices change
UPDATE positions SET market_value = quantity * current_price WHERE market_value = 0;

-- =============================================================================
-- PRICE_POINTS TABLE - Unified Model  
-- =============================================================================

-- Add alias column for API compatibility
ALTER TABLE price_points
ADD COLUMN IF NOT EXISTS date_time TIMESTAMP;

-- Sync timestamp to date_time for API compatibility
UPDATE price_points SET date_time = timestamp WHERE date_time IS NULL;

-- Add price column as alias for close
ALTER TABLE price_points
ADD COLUMN IF NOT EXISTS price DOUBLE PRECISION;

UPDATE price_points SET price = close WHERE price IS NULL;

-- =============================================================================
-- TIME_INTERVAL - Add missing values
-- =============================================================================

-- Update CHECK constraint to use standard intervals
ALTER TABLE price_points DROP CONSTRAINT IF EXISTS price_points_interval_check;
ALTER TABLE price_points ADD CONSTRAINT price_points_interval_check 
    CHECK (interval IN ('INTRADAY', 'DAILY', 'WEEKLY', 'MONTHLY'));

-- =============================================================================
-- PORTFOLIO TABLE - Unified Model
-- =============================================================================

-- Add calculated fields that API expects
ALTER TABLE portfolios
ADD COLUMN IF NOT EXISTS total_value DOUBLE PRECISION DEFAULT 0.0,
ADD COLUMN IF NOT EXISTS cash DOUBLE PRECISION DEFAULT 0.0;

-- Initialize cash to 0 for existing records
UPDATE portfolios SET cash = 0.0 WHERE cash IS NULL;
UPDATE portfolios SET total_value = 0.0 WHERE total_value IS NULL;

-- =============================================================================
-- USER TABLE - Keep database version (no conflicts with API)
-- =============================================================================
-- No changes needed - API doesn't have conflicting User entity

-- =============================================================================
-- TRADE TABLE - Keep database version (no conflicts with API)
-- =============================================================================
-- No changes needed - API doesn't have conflicting Trade entity

COMMIT;
