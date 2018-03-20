#BoS Language


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
