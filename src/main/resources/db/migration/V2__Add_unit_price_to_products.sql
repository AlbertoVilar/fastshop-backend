-- V2: Add unit_price to products and migrate existing data from price
-- This migration keeps backward compatibility by copying values from price and then making unit_price NOT NULL.

-- 1) Add column if not exists (safe re-run protection for dev environments)
ALTER TABLE products ADD COLUMN IF NOT EXISTS unit_price NUMERIC(12,2);

-- 2) Copy data from legacy column price when unit_price is null
UPDATE products SET unit_price = COALESCE(unit_price, price);

-- 3) Enforce NOT NULL for unit_price
ALTER TABLE products ALTER COLUMN unit_price SET NOT NULL;

-- 4) Optional: Drop legacy price column if it exists and is not used anymore
-- Uncomment the next line if the codebase no longer references price
-- ALTER TABLE products DROP COLUMN IF EXISTS price;

-- 5) Add a check constraint to ensure positive values
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'products_unit_price_positive'
    ) THEN
        ALTER TABLE products ADD CONSTRAINT products_unit_price_positive CHECK (unit_price >= 0);
    END IF;
END$$;