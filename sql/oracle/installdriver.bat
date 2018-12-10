REM Manual registration of Oracle driver in local maven repository.
REM Assumes default location of Oracle XE 10.2.0.
REM That location can be overwritten by setting DB_HOME environment variable.
REM Note that setting ORACLE_HOME breaks XE.

if not "%DB_HOME%"=="" goto install
REM set DB_HOME=C:\oraclexe\app\oracle\product\10.2.0
set DB_HOME=C:\app\%USERNAME%\product\11.2.0\dbhome_1
	
:install
mvn install:install-file -Dfile=%DB_HOME%\jdbc\lib\ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0 -Dpackaging=jar
