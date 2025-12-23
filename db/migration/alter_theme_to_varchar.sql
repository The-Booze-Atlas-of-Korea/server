-- Migration script for existing database
-- Apply ONLY if database already exists with ENUM theme

-- Step 1: Check for NULL/empty themes
-- SELECT COUNT(*) FROM drinking_plan WHERE theme IS NULL OR TRIM(theme) = '';

-- Step 2: Fill NULL/empty themes with UNKNOWN
UPDATE drinking_plan 
SET theme = 'UNKNOWN' 
WHERE theme IS NULL OR TRIM(theme) = '';

-- Step 3: Change ENUM to VARCHAR NOT NULL
ALTER TABLE drinking_plan MODIFY theme VARCHAR(50) NOT NULL;
