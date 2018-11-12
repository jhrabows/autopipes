REM Manual registration of Oracle driver in local maven repository.
REM Assumes default location of Oracle XE 10.2.0.
REM That location can be overwritten by setting DB_HOME environment variable.
REM Note that setting ORACLE_HOME breaks XE.

if not "%DB_HOME%"=="" goto install
set DB_HOME=C:\oraclexe\app\oracle\product\10.2.0
	
:install
mvn install:install-file -Dfile=%DB_HOME%\server\jdbc\lib\ojdbc14.jar -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0 -Dpackaging=jar
