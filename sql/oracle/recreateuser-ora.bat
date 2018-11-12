REM Drops and recreates 'autopipes' user with appropriate permissions in local Oracle instance.
REM No other Oracle objects are created here
REM Assumes 'autopipes' as system password
sqlplus "sys/autopipes as SYSDBA" @recreateuser-ora.sql autopipes
