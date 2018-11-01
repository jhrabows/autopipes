if "%DB_HOME%"=="" goto error
mvn install:install-file -Dfile=%DB_HOME%\server\jdbc\lib\ojdbc14.jar -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0 -Dpackaging=jar
:error
echo "DB_HOME must be set"