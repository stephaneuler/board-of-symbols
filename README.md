# board-of-symbols (BoSym)
Board of Symbols (BoSym, up to 2021 BoS) is an application for teaching (learning) programming. 
Using a set of given functions small code snippets produce patterns on a NxM board.
For example the following JS-Code

    for( x=1; x<6;  x++ ) {
      for( y=x; y<6; y++ ) {
        farbe2( x, y, BLUE )  // EN: color2(x,y,BLUE)
        if( x == y ) {
          form2( x, y, "tlu" )
        } else {
          form2( x, y, "s" )
       } 
      }
    }
 
 generates  
 
![BoS Screen](BoS.PNG)

The example uses the German version. You can change the language in the menu Optionen. This also changes the function names. 

## How to start - Java:
- download the file jserver.jar (older or newer version named jserver_xxx.jar have to be renamed to jserver.jar after download)
- if necessary - install java SDK
- run jserver.jar ( double click or via commandline: java -jar jserver.jar ) 
- then open a code window (menu "Fenster für Code-Eingabe" or ALT-c) to enter some Java code
- if javac is not found: add the sdk to the PATH or set the directory name (bin) in the properties menu in CodeWindow

A short video with the first steps: https://www.youtube.com/watch?v=VLVigtQNIeU&feature=youtu.be

## Important hints
BoSym requires a Java runtime (JRE) and a compiler (SDK). 
- if necessary, install a SDK
- BoSym needs to find the command javac. If the bin-directory is not in the Path, add it in the properties menue
- different versions of SDK and JRE can cause problems. Make sure that they have the same version. 
- starting BoSym with double click on jserver.jar sometime runs into problems. Try starting it from the command shell. 


## How to start - C
See [here](documentation/installing-c.md)
