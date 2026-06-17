-- Supply Chain Intelligence Platform
-- PostgreSQL initialization script
-- Runs once when Docker container first starts

-- Create schema if not exists
CREATE SCHEMA IF NOT EXISTS public;

-- Grant privileges
GRANT ALL PRIVILEGES ON SCHEMA public TO scplatform;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO scplatform;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO scplatform;

-- Note: Hibernate will auto-create tables via ddl-auto=update
-- This script just ensures the schema and permissions are correct
