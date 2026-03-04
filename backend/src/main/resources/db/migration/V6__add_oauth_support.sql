-- Make password nullable to support OAuth2 users who have no password
ALTER TABLE users ALTER COLUMN password DROP NOT NULL;

-- Add provider column to track auth provider (LOCAL, GOOGLE, GITHUB)
ALTER TABLE users ADD COLUMN provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL';

-- Add provider_id column to store the user's ID from the OAuth2 provider
ALTER TABLE users ADD COLUMN provider_id VARCHAR(255);

-- Unique constraint: one account per (provider, provider_id) pair
CREATE UNIQUE INDEX users_provider_provider_id_idx
    ON users (provider, provider_id)
    WHERE provider_id IS NOT NULL;
