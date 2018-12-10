# Autopipes - AutoCAD Macro for Sprinkler Systems
Preparation to install a sprinker system consist of
1. Creation of a Floor Plan (assume AutoCAD as the drawing tool)
2. Adding pipes, heads, e.t.c. to the Floor Plan drawing
3. Attaching pipe dimensions to the drawing for the installers
4. Creating inventory of all the pipes to be purchased and cut (known as cut-sheet)

The aim of this project is to automate steps 3-4 above. The project consists of
1. VBA Macro which allows the designer to interact with Autopipes from within AutoCAD
2. Web Service (Java) which receives details about the drawing from the VBA Macro, performs the necessary calculations and sends the resulting measures back to the Macro for display in the drawing
3. Web Page which displays the cut-sheets

# Build and deploy notes for developers
Required tools are
1. Java 10+
2. Maven 3.5.4+
3. Tomcat 9.0.12+ binary zip
4. Oracle XE 11+ (https://www.oracle.com/technetwork/database/enterprise-edition/downloads/112010-win64soft-094461.html)
5. SourceTree
6. Eclipse
7. Sql Developer (use SID=orcl to connect to XE)

All installations are standard except for these
1. Tomcat needs to run on port 9090 (to avoid conflict with Oracle XE). Hook up Tomcat to Eclipse using New Server wizard. This allows starting/debugging from Eclipse. Do not select any projects for deploy. Assuming that autoreload is set (default), deployment will be configured in pom 'install' lifecycle. Double-click on the created server in the 'Servers' tab and make these changes
a. change HTTP/1.1 port to 9090
b. change Server Locations to 'Use Tomcat installation'
c. change Publishing to Never publish

2. ojdbc jar needs to be installed in maven. Run installdriver script in sql/oracle folder. That script finds location of Oracle installation from an environment variable DB_HOME. If not set, defaults to location

C:\app\janhr\product\11.2.0\dbhome_1

3. Service expects an Oracle user with userid/password set to 'autopipes/autopipes'. Open sql/oracle folder and run

recreateuser-ora [db-owner]

That script assumes that the system password (provided during installation of OracleXE) is 'Autopipes3'. If a different password is used, the script needs to be adjusted manually. If db-owner is not provided, it will default to 'autopipes'.

Maven needs to know about
1. Java version of the source/target.
2. Location of Tomcat installation (for deploy)

This can be done by setting environment variable

MAVEN_OPTS=-Dmaven.compiler.source=1.10 -Dmaven.compiler.target=1.10 -Dtomcat.home.dir=[path-to-tomcat-home]

To build and deploy
1. Unzip or clone this project in a location of your choice
2. Run the following from the command line opened at top level
mvn clean install

