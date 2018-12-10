REM Drops and recreates 'autopipes' user with appropriate permissions in local Oracle instance.
REM No other Oracle objects are created here
REM Assumes 'Autopipes3' as system password
REM Owner defaults to autopipes
SET OWNER=%1
if not "%OWNER%"=="" goto install
SET OWNER=autopipes
:install
sqlplus "sys/Autopipes3 as SYSDBA" @recreateuser-ora.sql %OWNER%
