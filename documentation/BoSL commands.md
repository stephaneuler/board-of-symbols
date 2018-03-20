# BoS Language
The board receives commands in the BoS language (BoSL). 
In most cases the programmer does not use this language directly. 
For all basic commands functions are provided. 
The function *color2(2,3,RED)* for example changes the color of the symbol at position *(2,3)*. 
Internally the function sends the BoSL command *# 2 3 0xff0000 \n*.

Usually users do not need to use the BoSL commands directly. 
There are, however, a number of advanced commands without cooresponding function wrapper. 

    `board.receiveMessage( command );`


## List of additional commands

* `n` toggles numbering of symbols
* `s` returns the size of the board
* `sleep n` BoS waits for the given time (in milliseconds)
* `image` *x* *y* *filename* shows the image from the give file  at the position *(x,y)*.
The image is scaled to the size of the symbol (that can be changed with the function symbolSize(). 
*  `image` *x* *y* -` removes the image at position *(x,y)*
*  `bgImage` *filename* use the image as background (no scaling)
*  `fontsize` *n* sets the font size for symbol texts
*  `clearAllText` removes all texts
*  `statusfontsize` *n* sets the font size for the status text
*  `fonttype` *name* set the font for symbol texts
*  `symbolSizes` *r* changes the size of all symbols
*  *m1 f1 m2 f2 m3 f3 ...* (a sequence of pairs index - color) changes the color of the specified fields
*  `clearCommands` clears the command queue
*  `button` *text* *region*  adds a button in the given region (\emph{east}, \emph{south}, \emph{west}, \emph{north}). A click on this button adds the text to the command queue. 
*  `clearAllButtons` removes all buttons
*  `toggleInput` toggles the input field
