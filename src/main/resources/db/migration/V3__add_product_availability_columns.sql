-- Add is_available column to track product availability
ALTER TABLE product 
ADD COLUMN is_available BOOLEAN NOT NULL DEFAULT FALSE;

-- Add last_checked_at column to track when availability was last checked
ALTER TABLE product 
ADD COLUMN last_checked_at BIGINT;

-- Add index for is_available column for faster filtering
CREATE INDEX idx_availability ON product(is_available);

-- Add index for last_checked_at column for scheduler queries
CREATE INDEX idx_last_checked ON product(last_checked_at);