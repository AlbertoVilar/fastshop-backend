-- V3: Add created_at column to products with default current timestamp

-- 1) Add column if not exists
ALTER TABLE products ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;

-- 2) Backfill existing rows where created_at is null
UPDATE products SET created_at = NOW() WHERE created_at IS NULL;

-- 3) Enforce NOT NULL and default for new rows
ALTER TABLE products ALTER COLUMN created_at SET NOT NULL;
ALTER TABLE products ALTER COLUMN created_at SET DEFAULT NOW();