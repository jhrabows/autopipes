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
2. Mavan 3.5.4+
3. Tomcat 9.0.12+
4. Oracle XE 11+

All installations are standard except for these
1. Tomcat needs to run on port 9090 (to avoid conflict with Oracle XE)
2. ojdbc14.jar needs to be copied manually from C:\oraclexe\app\oracle\product\10.2.0\server\jdbc\lib to Tomcat's lib

Maven needs to know about
1. Java version of the source/target.
2. Location of Tomcat installation (for deploy)

This can be done by setting environment variable

MAVEN_OPTS=-Dmaven.compiler.source=1.10 -Dmaven.compiler.target=1.10 -Dtomcat.deploy.dir=[path-to-tomcat-webapps]

