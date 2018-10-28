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

# Build notes for developers
This project was build with java 10 and mavan 3.5.4
Maven needs to be told about java version of the source. This can be done by setting environment variable

MAVEN_OPTS=-Dmaven.compiler.source=1.6 -Dmaven.compiler.target=1.6
