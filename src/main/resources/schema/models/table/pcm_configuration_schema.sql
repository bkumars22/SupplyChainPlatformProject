-- ============================================================================
-- PCM Configuration Database Schema
-- Creates table, sequence, and triggers for configuration management
-- Database: Oracle
-- ============================================================================

-- Drop existing objects if they exist (for clean installation)
-- In production, remove these DROP statements

-- Drop trigger if exists
BEGIN
   EXECUTE IMMEDIATE 'DROP TRIGGER PCM_CONFIGURATION_TRG';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

-- Drop sequence if exists
BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE PCM_CONFIGURATION_SEQ';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

-- Drop table if exists
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE PCM_CONFIGURATION CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

-- ============================================================================
-- Create Sequence for Primary Key
-- ============================================================================
CREATE SEQUENCE PCM_CONFIGURATION_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- ============================================================================
-- Create Configuration Table
-- ============================================================================
CREATE TABLE PCM_CONFIGURATION (
    ID              NUMBER(19)      NOT NULL,
    CONFIG_KEY      VARCHAR2(500)   NOT NULL,
    CONFIG_VALUE    VARCHAR2(4000),
    DESCRIPTION     VARCHAR2(2000),
    VALUE_TYPE      VARCHAR2(50)    DEFAULT 'STRING',
    IS_ACTIVE       NUMBER(1)       DEFAULT 1,
    CREATED_DATE    TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    MODIFIED_DATE   TIMESTAMP,
    CREATED_BY      VARCHAR2(100),
    MODIFIED_BY     VARCHAR2(100),
    
    CONSTRAINT PK_PCM_CONFIGURATION PRIMARY KEY (ID),
    CONSTRAINT UK_PCM_CONFIG_KEY UNIQUE (CONFIG_KEY),
    CONSTRAINT CHK_VALUE_TYPE CHECK (VALUE_TYPE IN ('STRING', 'BOOLEAN', 'LIST', 'INTEGER', 'DOUBLE'))
);

-- Create Index on CONFIG_KEY for faster lookups
CREATE INDEX IDX_PCM_CONFIG_KEY ON PCM_CONFIGURATION(CONFIG_KEY);

-- Create Index on IS_ACTIVE for filtering active configurations
CREATE INDEX IDX_PCM_CONFIG_ACTIVE ON PCM_CONFIGURATION(IS_ACTIVE);

-- ============================================================================
-- Create Trigger for Auto-populating ID and Timestamps
-- ============================================================================
CREATE OR REPLACE TRIGGER PCM_CONFIGURATION_TRG
    BEFORE INSERT OR UPDATE ON PCM_CONFIGURATION
    FOR EACH ROW
BEGIN
    -- Auto-generate ID on INSERT if not provided
    IF INSERTING THEN
        IF :NEW.ID IS NULL THEN
            :NEW.ID := PCM_CONFIGURATION_SEQ.NEXTVAL;
        END IF;
        -- Set CREATED_DATE if not provided
        IF :NEW.CREATED_DATE IS NULL THEN
            :NEW.CREATED_DATE := SYSTIMESTAMP;
        END IF;
    END IF;
    
    -- Always update MODIFIED_DATE on UPDATE
    IF UPDATING THEN
        :NEW.MODIFIED_DATE := SYSTIMESTAMP;
    END IF;
END;
/

-- ============================================================================
-- Add Comments to Table and Columns
-- ============================================================================
COMMENT ON TABLE PCM_CONFIGURATION IS 'Stores PCM application configuration as key-value pairs';
COMMENT ON COLUMN PCM_CONFIGURATION.ID IS 'Primary key - auto-generated sequence';
COMMENT ON COLUMN PCM_CONFIGURATION.CONFIG_KEY IS 'Unique configuration property key';
COMMENT ON COLUMN PCM_CONFIGURATION.CONFIG_VALUE IS 'Configuration value - can be string, boolean, or comma-separated list';
COMMENT ON COLUMN PCM_CONFIGURATION.DESCRIPTION IS 'Description of the configuration property';
COMMENT ON COLUMN PCM_CONFIGURATION.VALUE_TYPE IS 'Type of value: STRING, BOOLEAN, LIST, INTEGER, DOUBLE';
COMMENT ON COLUMN PCM_CONFIGURATION.IS_ACTIVE IS 'Flag indicating if configuration is active (1) or inactive (0)';
COMMENT ON COLUMN PCM_CONFIGURATION.CREATED_DATE IS 'Timestamp when record was created - auto-populated by trigger';
COMMENT ON COLUMN PCM_CONFIGURATION.MODIFIED_DATE IS 'Timestamp when record was last modified - auto-updated by trigger';
COMMENT ON COLUMN PCM_CONFIGURATION.CREATED_BY IS 'User who created the record';
COMMENT ON COLUMN PCM_CONFIGURATION.MODIFIED_BY IS 'User who last modified the record';

-- ============================================================================
-- Grant Permissions (adjust roles as needed)
-- ============================================================================
-- GRANT SELECT, INSERT, UPDATE, DELETE ON PCM_CONFIGURATION TO PCM_APP_ROLE;
-- GRANT SELECT ON PCM_CONFIGURATION_SEQ TO PCM_APP_ROLE;

COMMIT;
/

-- Verify table creation
SELECT table_name, num_rows FROM user_tables WHERE table_name = 'PCM_CONFIGURATION';

-- Verify sequence creation
SELECT sequence_name, last_number FROM user_sequences WHERE sequence_name = 'PCM_CONFIGURATION_SEQ';

-- Verify trigger creation
SELECT trigger_name, status FROM user_triggers WHERE trigger_name = 'PCM_CONFIGURATION_TRG';
