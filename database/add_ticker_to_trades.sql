-- Add ticker column to trades table for denormalization
-- This improves query performance by avoiding joins for common operations

-- Add the ticker column (nullable initially for existing data)
ALTER TABLE public.trades 
ADD COLUMN IF NOT EXISTS ticker text;

-- Populate ticker from companies table for existing trades
UPDATE public.trades t
SET ticker = c.ticker
FROM public.companies c
WHERE t.company_id = c.id
AND t.ticker IS NULL;

-- Create index on ticker for performance
CREATE INDEX IF NOT EXISTS idx_trades_ticker ON public.trades(ticker);

-- Add a trigger to auto-populate ticker on insert
CREATE OR REPLACE FUNCTION public.set_trade_ticker()
RETURNS TRIGGER AS $$
BEGIN
  -- Auto-populate ticker from companies table
  IF NEW.ticker IS NULL AND NEW.company_id IS NOT NULL THEN
    SELECT ticker INTO NEW.ticker
    FROM public.companies
    WHERE id = NEW.company_id;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger
DROP TRIGGER IF EXISTS trigger_set_trade_ticker ON public.trades;
CREATE TRIGGER trigger_set_trade_ticker
  BEFORE INSERT ON public.trades
  FOR EACH ROW
  EXECUTE FUNCTION public.set_trade_ticker();

-- Verify the changes
SELECT 
  column_name, 
  data_type, 
  is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' 
  AND table_name = 'trades'
  AND column_name = 'ticker';
