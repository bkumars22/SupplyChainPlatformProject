CREATE TABLE MTCM_PURGE_LOG
   ( LOG_ID NUMBER(10,0), 
	APP_ID VARCHAR2(50 BYTE), 
	TABLE_NAME VARCHAR2(50 BYTE), 
	KEY_COLUMN VARCHAR2(80 BYTE), 
	PROGRAM_NAME VARCHAR2(32 BYTE), 
	STATUS VARCHAR2(15 BYTE), 
	START_DATE DATE, 
	END_DATE DATE, 
	LAST_UPDATED DATE, 
	ROWS_PROCESSED NUMBER(10,0), 
	MESSAGE VARCHAR2(3000 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE AUDIT_DATA ;
   
CREATE SEQUENCE MTCM_PURGE_LOG_SEQ  MINVALUE 1 MAXVALUE 999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE ;

create or replace PACKAGE MTCM_PURGE_PKG AS

  g_ProcName CONSTANT VARCHAR2(30) := 'MTCM_PURGE_PKG.TABLE_PURGE';

  PROCEDURE LOG_PURGE_STATUS ( pin_LogId          IN  VARCHAR2,
                               pin_Message        IN  VARCHAR2,
                               pin_Status         IN  VARCHAR2,
                               pin_RowsProcessed  IN  NUMBER
                               );

  PROCEDURE TABLE_PURGE ( pin_AppPurgeID    IN   VARCHAR2,
                          pin_TableName     IN   VARCHAR2,
                          pin_KeyColumn     IN   VARCHAR2,
                          pin_RetentionDays IN   NUMBER,
                          pin_ArchiveDays   IN   NUMBER,
                          pout_Elapsed      OUT  NUMBER
                          );
END;
/

CREATE OR REPLACE PACKAGE BODY MTCM_PURGE_PKG AS
   PROCEDURE LOG_PURGE_STATUS (pin_LogId          IN  VARCHAR2,
                               pin_Message        IN  VARCHAR2,
                               pin_Status         IN  VARCHAR2,
                               pin_RowsProcessed  IN  NUMBER
                               )
  --******************************************************************************
  -- Stored Procedure: LOG_PURGE_STATUS
  --
  -- Description: Log purge status in PURGE_LOG table
  --
  -- Parameters: pin_AppPurgeID - Primary Key of E2MC_PURGE_LOG
  --          pin_DbOperation - Db operation PURGE / ARCHIVE
  --          pin_Message - Log message
  --          pin_Status - Log message status (STARTED/COMPLETED/ERROR)
  --          pin_RowsPurged - Rows purged so far
  --
  -- Version History
  -- ----------------------------------------------------------------------------
  -- Version | MTCM ver | Date      | Comments                       | Who
  -- ----------------------------------------------------------------------------
  -- 1.0.0   24.2        2024-04-04   Created                          CKU
  --*****************************************************************************
  IS
    PRAGMA AUTONOMOUS_TRANSACTION;                                          -- This Procedure execution will be independent transaction than the calling procedure
    c_MsgDate               CONSTANT DATE := SYSDATE;                       -- message log date, current sysdate
    c_StoredProcedureName   CONSTANT  VARCHAR2(20) := 'LOG_PURGE_STATUS';   -- Current stored procedure name
    v_ErrorText             VARCHAR2(2048);                                 -- Textual details of errors
    v_IntErrorStatus        INTEGER;                                        -- Number of any internal error generated
    v_RowExists             INTEGER;                                        -- Check if row already exists
  BEGIN
    IF ( pin_Status != 'COMPLETED') THEN
      EXECUTE IMMEDIATE 'UPDATE MTCM_PURGE_LOG
                            SET STATUS = ''' || pin_Status  || ''',
                                MESSAGE = ''' || pin_Message || ''',
                                ROWS_PROCESSED = '|| NVL(pin_RowsProcessed,0) || ',
                                LAST_UPDATED = SYSDATE
                          WHERE LOG_ID = ' || pin_LogId;
     COMMIT;
    ELSE
      EXECUTE IMMEDIATE 'UPDATE MTCM_PURGE_LOG
                            SET STATUS = ''' || pin_Status  || ''',
                                MESSAGE = ''' || pin_Message || ''',
                                ROWS_PROCESSED  = '|| NVL(pin_RowsProcessed,0) || ',
                                LAST_UPDATED = SYSDATE,
                                END_DATE = SYSDATE
                          WHERE LOG_ID = ' || pin_LogId ;
     COMMIT;
    END IF;
  EXCEPTION
    WHEN OTHERS THEN
      v_IntErrorStatus := 200;
      v_ErrorText := 'Unable to UPDATE information to MTCM_PURGE_LOG table, The error returned was ''' || SQLERRM || ''';';
      RAISE_APPLICATION_ERROR(-20000, c_StoredProcedureName || ' ERROR-' || TO_CHAR(v_IntErrorStatus) || ': ' || v_ErrorText, TRUE);
  END LOG_PURGE_STATUS;

  PROCEDURE TABLE_PURGE ( pin_AppPurgeID     IN   VARCHAR2,
                          pin_TableName      IN   VARCHAR2,
                          pin_KeyColumn      IN   VARCHAR2,
                          pin_RetentionDays  IN   NUMBER,
                          pin_ArchiveDays    IN   NUMBER,
                          pout_Elapsed       OUT  NUMBER
                          )
  --******************************************************************************
  -- Stored Procedure: TABLE_PURGE
  --
  -- Description: Purges table data based on input parameters
  --
  -- Parameters: pin_AppPurgeID - UUID passed by application
  --          pin_TableName - Table that needs to be purged
  --          pin_KeyColumn - Key column in the table used for purge
  --          pin_RetentionDays - Number of days of data to be retained, delete remaining
  --          pin_ArchiveDays - Number of days of data post which partitoins will be compressed
  --          pout_Elapsed - (OUT parameter) Time taken for the purge operation
  --                        for the table
  --
  -- Version History
  -- -----------------------------------------------------------------------------
  -- Version | MTCM ver | Date      | Comments                       | Who
  -- -----------------------------------------------------------------------------
  -- 1.0.0    24.2       2024-04-04   Created                          CKU
  --******************************************************************************
  IS
    -- Variables
    v_StartTime             NUMBER;                           -- Start time to get elapsed time
    v_EndTime               NUMBER;                           -- End time to get elapsed time
    v_ElapsedSec            NUMBER;                           -- Elapsed seconds
    v_Sql                   VARCHAR2(2000);                   -- Store SQL query
    v_TableName             VARCHAR2(50);                     -- Table name
    v_TableNameExists       NUMBER(1);                        -- Table name exists
    v_KeyColumn             VARCHAR2(80);
    v_KeyColumnExists       NUMBER(1);
    v_KeyColDataType        VARCHAR2(50);
    v_Days                  NUMBER;
    v_RetentionDays         NUMBER;
    v_ArchiveDays           NUMBER;
    v_PurgeDate             DATE;
    v_ArchiveDate           DATE;
    v_CompressDate          DATE;
    v_Batch                 NUMBER;
    v_RowsUpdCount          NUMBER;
    v_RowsCount             NUMBER;
    v_RowsDelCount          NUMBER;
    v_ErrorText             VARCHAR2(2000);
    v_InternalError         NUMBER;
    v_LogId                 NUMBER;
    v_LogRowExists          NUMBER;
    v_PartitionRowCount     NUMBER;
    v_InitialPartitionCount NUMBER;
    v_PartitionCount        NUMBER;
    v_PartitionDropped      NUMBER;
    v_PartitionCompressed   NUMBER;
    v_RowsCompressCount     NUMBER;
  BEGIN
    DBMS_OUTPUT.ENABLE ( BUFFER_SIZE => NULL );
    v_TableName     := pin_TableName;
    v_KeyColumn     := pin_KeyColumn;
    v_RetentionDays := pin_RetentionDays;  -- Number of days of data to be retained, delete remaining
    v_ArchiveDays   := pin_ArchiveDays;    -- Number of days of data post which partitoins will be compressed
    -- Check if input table name parameter is valid
    v_TableNameExists := 0;
    BEGIN
      SELECT 1
        INTO v_TableNameExists
        FROM DUAL
       WHERE EXISTS
             ( SELECT TABLE_NAME
                 FROM USER_TABLES
                WHERE TABLE_NAME = v_TableName )
         AND ROWNUM < 2;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_InternalError := 100 ;
        v_ErrorText := 'Specified table name ''' || v_TableName || ''' doesnt exists. ';
        RAISE_APPLICATION_ERROR(-20000, g_ProcName || ' ERROR-' || TO_CHAR(v_InternalError) || ': ' || v_ErrorText, TRUE);
    END;
    -- Check if input column name parameter is valid
    BEGIN
      v_KeyColumnExists := 0;
      SELECT 1
        INTO v_KeyColumnExists
        FROM DUAL
       WHERE EXISTS
             ( SELECT COLUMN_NAME
                 FROM USER_TAB_COLUMNS
                WHERE TABLE_NAME = v_TableName
                  AND COLUMN_NAME = v_KeyColumn )
         AND ROWNUM < 2;
    EXCEPTION
       WHEN NO_DATA_FOUND THEN
         v_InternalError := 101 ;
         v_ErrorText := 'Specified column name ''' || v_KeyColumn || ''' in table name ''' || v_TableName || ''' doesnt exists. ';
         RAISE_APPLICATION_ERROR(-20000, g_ProcName || ' ERROR-' || TO_CHAR(v_InternalError) || ': ' || v_ErrorText, TRUE);
    END;
    SELECT DATA_TYPE
      INTO v_KeyColDataType
      FROM USER_TAB_COLUMNS
     WHERE TABLE_NAME = v_TableName
       AND COLUMN_NAME = v_KeyColumn;
    IF (v_KeyColDataType NOT LIKE 'DATE%' AND v_KeyColDataType NOT LIKE 'TIMESTAMP%') THEN
      v_InternalError := 102;
      v_ErrorText := 'Data type of specified column name ''' || v_KeyColumn || ''' in table name ''' || v_TableName || ''' is ''' || v_KeyColDataType ||''', specify DATE or TIMESTAMP column.' ;
      RAISE_APPLICATION_ERROR(-20000, g_ProcName || ' ERROR-' || TO_CHAR(v_InternalError) || ': ' || v_ErrorText, TRUE);
    END IF;
    -- Check if the AppPurgeID and TableName are not present
    v_LogRowExists := 0;
    BEGIN
       SELECT 1
         INTO v_LogRowExists
         FROM DUAL
        WHERE EXISTS
              ( SELECT LOG_ID
                  FROM MTCM_PURGE_LOG
                 WHERE APP_ID = pin_AppPurgeID
                   AND TABLE_NAME = v_TableName )
          AND ROWNUM < 2;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        NULL;
    END;
    IF (v_LogRowExists = 1) THEN
      v_InternalError := 103;
      v_ErrorText := 'There is already purge entry in log table MTCM_PURGE_LOG for App Purge Id  ''' || pin_AppPurgeID || ''' and Table ''' || v_TableName || ''', please check.' ;
      RAISE_APPLICATION_ERROR(-20000, g_ProcName || ' ERROR-' || TO_CHAR(v_InternalError) || ': ' || v_ErrorText, TRUE);
    END IF;
    BEGIN
      SELECT COUNT(*)
        INTO v_InitialPartitionCount
        FROM USER_TAB_PARTITIONS
       WHERE TABLE_NAME = v_TableName ;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_InternalError := 104;
        v_ErrorText := 'Table ''' || v_TableName || ''' is not partitioned, please check.' ;
        RAISE_APPLICATION_ERROR(-20000, g_ProcName || ' ERROR-' || TO_CHAR(v_InternalError) || ': ' || v_ErrorText, TRUE);
    END;
    -- Start of Purge
    -- Generate Primary key for MTCM_PURGE_LOG for this job
    SELECT MTCM_PURGE_LOG_SEQ.NEXTVAL
      INTO v_LogId
      FROM DUAL;
    -- Insert entry in to MTCM_PURGE_LOG
    INSERT
      INTO MTCM_PURGE_LOG ( LOG_ID,
                            APP_ID,
                            TABLE_NAME,
                            KEY_COLUMN,
                            PROGRAM_NAME,
                            STATUS,
                            START_DATE,
                            END_DATE,
                            LAST_UPDATED,
                            ROWS_PROCESSED,
                            MESSAGE
                            )
                  VALUES  ( v_LogId,
                            pin_AppPurgeID,
                            v_TableName,
                            v_KeyColumn,
                            'PURGE',
                            'STARTED',
                            SYSDATE,
                            SYSDATE,
                            SYSDATE,
                            '00',
                            'Purge Program Started'
                            );
    COMMIT;
    v_StartTime := DBMS_UTILITY.GET_TIME();
    BEGIN
      -- Convert days parameter to date
      v_PurgeDate         := TO_DATE(SYSDATE - v_RetentionDays);
      v_Batch             := 0 ;
      v_RowsDelCount      := 0 ;
      v_PartitionDropped  := 0 ;
      v_PartitionRowCount := 0 ;
      v_PartitionCount    := 0 ;
      -- partitions on a table
      FOR x IN (SELECT TABLE_NAME,
                       PARTITION_NAME,
                       TO_DATE( SUBSTR( EXTRACTVALUE(dbms_xmlgen.getxmltype('SELECT HIGH_VALUE
                                                                               FROM USER_TAB_PARTITIONS
                                                                              WHERE TABLE_NAME = ''' ||t.TABLE_NAME || ''' and PARTITION_NAME = ''' || t.PARTITION_NAME || '''' ), '//text()'), 12,10), 'YYYY-MM-DD') AS HIGH_VALUE
                  FROM USER_TAB_PARTITIONS t WHERE TABLE_NAME = upper(v_tablename))
      LOOP
        -- date less than the given date
        IF (TO_DATE(x.HIGH_VALUE) <= TRUNC(v_PurgeDate)) THEN
          v_Batch := v_Batch + 1 ;
          -- checking for the count of records per partition
          v_sql :=  'SELECT COUNT(*) FROM '||v_TableName ||' PARTITION ('||x.PARTITION_NAME||')';
          EXECUTE IMMEDIATE v_Sql INTO v_PartitionRowCount;
          v_RowsDelCount := v_RowsDelCount + v_PartitionRowCount;
          -- dropping the respective partition
          v_sql := 'ALTER TABLE '||v_TableName||' DROP PARTITION '||x.PARTITION_NAME||' UPDATE GLOBAL INDEXES ';
          EXECUTE IMMEDIATE v_sql;
          -- After dropping the partition check the count of partition exists
          v_sql :='SELECT COUNT(*) FROM ALL_TAB_PARTITIONS WHERE TABLE_NAME = '''||v_tablename||''' ';
          EXECUTE IMMEDIATE v_Sql INTO v_PartitionCount;
          -- Difference of partition count
          v_PartitionDropped := v_InitialPartitionCount - v_PartitionCount ;
          LOG_PURGE_STATUS(v_LogId, 'Purge running for ' || v_TableName || ', Number of rows deleted: ' || v_RowsDelCount || ', Number of partitions dropped: ' || v_PartitionDropped || ' .', 'STARTED', v_RowsDelCount);
        END IF;
      END LOOP;
      v_EndTime := DBMS_UTILITY.GET_TIME();
      v_ElapsedSec := ROUND((v_EndTime - v_StartTime)/100);
      LOG_PURGE_STATUS(v_LogId, 'Purge completed. Number of rows deleted: ' || v_RowsDelCount || ', Number of partitions dropped: ' || v_PartitionDropped || ' .', 'COMPLETED', v_RowsDelCount);
    EXCEPTION
      WHEN OTHERS THEN
        ROLLBACK;
        v_InternalError := 105;
        v_ErrorText := SQLERRM;
        LOG_PURGE_STATUS(v_LogId, 'Purge failed for table ' || v_TableName || ' with error '|| v_ErrorText ||  ' ', 'ERROR', v_RowsDelCount);
        RAISE_APPLICATION_ERROR(-20000, g_ProcName || ' ERROR-' || TO_CHAR(v_InternalError) || ': ' || SQLERRM, TRUE);
        COMMIT;
    END;
    -- Start of Archive
    -- Generate Primary key for MTCM_PURGE_LOG for this job
    SELECT MTCM_PURGE_LOG_SEQ.NEXTVAL
      INTO v_LogId
      FROM DUAL;
    -- Insert entry in to MTCM_PURGE_LOG
    INSERT
      INTO MTCM_PURGE_LOG ( LOG_ID,
                            APP_ID,
                            TABLE_NAME,
                            KEY_COLUMN,
                            PROGRAM_NAME,
                            STATUS,
                            START_DATE,
                            END_DATE,
                            LAST_UPDATED,
                            ROWS_PROCESSED,
                            MESSAGE
                            )
                   VALUES  ( v_LogId,
                             pin_AppPurgeID,
                             v_TableName,
                             v_KeyColumn,
                             'ARCHIVE',
                             'STARTED',
                             SYSDATE,
                             SYSDATE,
                             SYSDATE,
                             '00',
                             'Archive Program Started'
                             );
    COMMIT;
    v_StartTime := DBMS_UTILITY.GET_TIME();
    BEGIN
      -- Convert days parameter to date
      v_ArchiveDate   := TO_DATE(SYSDATE - pin_ArchiveDays);  -- Rentition Days
      --dbms_output.put_line('v_ArchiveDate : '|| v_ArchiveDate);
      v_Batch            := 0 ;
      v_RowsCompressCount   := 0 ;
      v_PartitionCompressed  := 0 ;
      v_PartitionRowCount   := 0 ;
      -- partitions on a table
      FOR x IN (SELECT TABLE_NAME,
                       PARTITION_NAME,
                       COMPRESSION,
                       TO_DATE( SUBSTR( EXTRACTVALUE(dbms_xmlgen.getxmltype('SELECT HIGH_VALUE
                                                                               FROM USER_TAB_PARTITIONS
                                                                              WHERE TABLE_NAME = ''' ||t.TABLE_NAME || ''' and PARTITION_NAME = ''' || t.PARTITION_NAME || '''' ), '//text()'), 12,10), 'YYYY-MM-DD') AS HIGH_VALUE
                  FROM USER_TAB_PARTITIONS t
                 WHERE TABLE_NAME = UPPER(v_tablename))
      LOOP
        -- date less than the given date
        IF (TO_DATE(x.HIGH_VALUE) <= TRUNC(v_ArchiveDate) AND x.COMPRESSION != 'ENABLED' ) THEN
          v_Batch := v_Batch + 1 ;
          -- checking for the count of records per partition
          v_sql :=  'SELECT COUNT(*) FROM '||v_TableName ||' PARTITION ('||x.PARTITION_NAME||')';
          EXECUTE IMMEDIATE v_Sql INTO v_PartitionRowCount;
          v_RowsCompressCount := v_RowsCompressCount + v_PartitionRowCount;
          -- dropping the respective partition
          v_sql := 'ALTER TABLE '||v_TableName||' MOVE PARTITION '||x.PARTITION_NAME||' COMPRESS ONLINE PARALLEL';
          EXECUTE IMMEDIATE v_sql;
          LOG_PURGE_STATUS(v_LogId, 'Archive running for ' || v_TableName || ', Number of rows compressed: ' || v_RowsCompressCount || ', Number of partitions compressed: ' || v_Batch || ' .', 'STARTED', v_RowsCompressCount);
        END IF;
      END LOOP;
      v_EndTime := DBMS_UTILITY.GET_TIME();
      v_ElapsedSec := ROUND((v_EndTime - v_StartTime)/100);
      LOG_PURGE_STATUS(v_LogId, 'Archive completed. Number of rows compressed: ' || v_RowsCompressCount || ', Number of partitions compressed: ' || v_Batch || ' .', 'COMPLETED', v_RowsCompressCount);
    EXCEPTION
      WHEN OTHERS THEN
        ROLLBACK;
        v_InternalError := 106;
        v_ErrorText := SQLERRM;
        LOG_PURGE_STATUS(v_LogId, 'Archive failed for table ' || v_TableName || ' with error '|| v_ErrorText ||  ' ', 'ERROR', v_RowsCompressCount);
        RAISE_APPLICATION_ERROR(-20000, g_ProcName || ' ERROR-' || TO_CHAR(v_InternalError) || ': ' || SQLERRM, TRUE);
        COMMIT;
    END;
  END TABLE_PURGE;
END MTCM_PURGE_PKG;
/
