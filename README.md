# board-of-symbols

how to start:
- download the zip-Archive
- unzip the archiv
- if necessary - install gcc (GNU C compiler) 
- run jserver.jar ( double click or command: java -jar jserver.jar ) 
- then open a code window (menu "Fenster für Code-Eingabe") to enter some C code

For Visual Studio users:
in order to execute the compile and link command cl some environment variables have to be set. The program reads these variables from the file vc.properties. You can build a version for your system as follows:
- open a command window
- go to the Visual Studio directories and run vsvarsall.bat script 
- in the command window return to the jserver directory
- type: set > vc.properties 
- open this file in an editor and replace all backslashs \  with double backslashs \\\\ 

Integration in a Visual Studio project: 
in the directory applicaton_vs you find all required .cpp and .h files. Coyp them into your project. 

In Xcode, create new CommandLine Application as ANSI C oder C++ and simply drop in all necessary files.

