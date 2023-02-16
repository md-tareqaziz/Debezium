# Debezium
Target is to send all the DB change logs into Audit table.
All configurations are reading from the properties file. 
And configurations are added in Router class. There is also a repository of audit entity.
to save changes to DB.

Some useful DB queries are:

    
    alter/create user user_name REPLICATION LOGIN;
    ALTER SYSTEM SET wal_level = logical;
    Note: Avobe queries required Super user permission
    
    SHOW wal_level;
    ALTER TABLE table_name REPLICA IDENTITY FULL;
    CREATE PUBLICATION dbz_publication2 FOR TABLE table1,table2,table3;
    CREATE ROLE name REPLICATION LOGIN;
    alter PUBLICATION dbz_publication2 add table profile_shift;
