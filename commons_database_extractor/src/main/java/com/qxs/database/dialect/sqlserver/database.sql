SELECT name FROM MASTER.DBO.SYSDATABASES where name not in ('master', 'model', 'msdb', 'tempdb') ORDER BY NAME