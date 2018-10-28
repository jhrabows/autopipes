# Autopipes VBA Macro
This subproject contains 2 version of the Autopipes VBA Macro. The ...v14.dvb works with AutoCAD version 14 and earlier. The ...v16.dvb works with AutoCAD version 16. The 2 files are identical exept for 3 location where we use method name ObjectIdToObject() for the version 16 and ObjectIdToObject32() for versions 14 and earlier. In both cases we assume 64-bit version of AutoCAD.
# Loading instructions
You need to run AutpCAD which supports VBA. In version 16, VBA support is installed separately.
When you open the first drawing you need to load the appropriate Macro using VBA Load Manager.
Then execute Run Macro command which opens a dialog with 3 tabs. The left tab is used to specify diameter information for the layers found in the drawing. The middle tab contains the buttons which result in calls to Autopipes service.