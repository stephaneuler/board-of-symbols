# List of commands

* `n` toggles numbering of symbols
*   s! gibt die Größe (Anzahl Zeilen und Spalten) zurück.
*   sleep n! Board wartet für die angegebene Zeit (Wert in Millisekunden).
*   image! $x$ $y$ $dateiname$ das Bild aus der angegebenen Datei wird an der Stelle $(x,y)$ angezeigt.
*  Dabei wird es auf die Größe des Symbols skaliert.
*  Ein eventuell an dieser Stelle bereits vorhandenes Bild wird gelöscht.
*   image! $x$ $y$ -! das Bild an der Stelle $(x,y)$ wird gelöscht.
*   bgImage! $dateiname$ das Bild wird als Hintergrund eingefügt.
*   fontsize! $n$ setzt die Font-Größe für alle weiteren Texte.
*   clearAllText! löscht alle Symbol-Texte.
*   statusfontsize! $n$ Setzt die Font-Größe für die Statuszeile.
*   fonttype! $name$ Wählt einen Font aus.
*   symbolSizes! $r$ verändert die Größe aller Symbole.
*   \emph{m1 f1 m2 f2 m3 f3} $\ldots$ (eine Folge von Paaren Feldnummer-Farbe), die angegebenen Felder werden in der jeweiligen Farbe gezeichnet.
*   clearCommands! löscht alle ausstehenden Eingaben.
*   button! $text$ $region$ legt einen Knopf (Button) in der angegebenen Region (\emph{east}, \emph{south}, \emph{west}, \emph{north}) an.
*  Beim Betätigen wird der angegebene Text als Eingabe geschickt.
*   clearAllButtons! entfernt alle angelegten Eingabe-Knöpfe.
*   toggleInput! blendet das Eingabefeld ein oder aus.
