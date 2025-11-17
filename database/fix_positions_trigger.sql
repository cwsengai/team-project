-- Fix trigger for portfolio_positions table
-- The column is called 'last_updated' not 'updated_at'

-- Drop the existing trigger
DROP TRIGGER IF EXISTS update_positions_last_updated ON portfolio_positions;

-- Create a new trigger function specific to positions
CREATE OR REPLACE FUNCTION update_last_updated_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_updated = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create the trigger using the new function
CREATE TRIGGER update_positions_last_updated BEFORE UPDATE ON portfolio_positions
    FOR EACH ROW EXECUTE FUNCTION update_last_updated_column();
