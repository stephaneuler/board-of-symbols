# board-of-symbols (BoS)

### Installation on Mac OS X (German)
##### Guide contributed by @rjhllr - Fall 2016
##### English version available [here!](./installing-on-os-x.md)

#### Vorwort

Diese Anleitung beschreibt die Installation und den Funktionstest der Board-of-Symbols (im Folgenden: BoS) Anfänger-Programmierumgebung auf OS X. Vorausgesetzt wird, dass als Programmiersprache C verwendet werden soll. BoS unterstützt auch Java, dies wird allerdings in dieser Anleitung noch nicht en detail beschrieben. Grundsätzlich muss die Installation von gcc durch die Installation des JDK (Java Development Kit) ersetzt werden. Diese Anleitung wurde auf OS X El Capitan (10.11) getestet.

> Als fortgeschrittener Nutzer kann die Installation von Homebrew übersprungen werden und die XCode Kommandozeilentools direkt installiert, BoS dann als ZIP-Archiv von GitHub geladen werden. Die Installation mit Homebrew scheint mir allerdings zukunftssicherer und git sollte ohnehin auf dem Computer eines jeden Entwicklers installiert sein.

---

#### Schritt 1 - Installation des JRE

Damit die BoS Hauptanwendung laufen kann, wird die Java Runtime Environment benötig. Diese gibt es direkt auf der [Oracle JRE Downloadseite](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html), einfach die Lizenzvereinbarung akzeptieren und die JRE runterladen

Diese Anleitung geht davon aus, dass die **.dmg** Version der JRE installiert wird.

Nach dem Download muss die heruntergeladene Datei geöffnet und der Installer 2x angeklickt werden. Darauf hin folgt man den Schritten des Installationsassistenen.

#### Schritt 2 - Installation von Homebrew

Homebrew muss gemäß der Anweisungen auf der [Homebrew Seite](http://brew.sh/index_de.html) installiert werden. 

Öffnen Sie zunächst das Terminal. Das geht über das Launchpad (F4), dort nach "Terminal" suchen. Was sich nun öffnet ist eine *Kommandozeile*, *terminal*, *shell* oder *command line*. Es ist eine textbasierte Möglichkeit, Aktionen auf dem Computer auszulösen und ein vielfältiges Werkzeug.

Kopieren Sie nun das Kommando von der Homebrew-Webseite:

    /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"

und drücken Sie **Enter**. Die Installation von Homebrew beginnt nun. Sie werden während der Installation nach Ihrem Accountpasswort gefragt.


#### Step 3 - Installation von git und gcc

Nachdem die Installation von Homebrew geglückt ist, setzen Sie folgendes Kommando im Terminal ab:

    brew install git gcc

Homebrew versucht nun git und gcc zu installieren. Sie werden nun danach gefragt, die XCode Kommandozeilentools zu installieren. Bitte bestätigen Sie diese Installation, denn gcc wird mit den XCode Kommandozeilentools installiert. Wenn die Installation ohne Fehler abgeschlossen wurde, können Sie weitermachen.

#### Step 4 - Runterladen von BoS

Immernoch im Terminal, erstellen Sie einen neuen Ordner:

    mkdir ~/Documents/BoS/ && cd ~/Documents/BoS

Diese 2 Kommandos erstellen einen neuen Ordner (*mkdir*, make directory) und wechselt das Arbeitsverzeichnis dorthin (*cd*, change directory).

    git clone https://github.com/stephaneuler/board-of-symbols.git --depth 1 ./

wird nun BoS in das aktuelle Arbeitsverzeichnis laden.

#### Step 5 - Test

Öffnen Sie den Finder und navigieren in das neu erstellte Verzeichnis "BoS" im Ordner "Dokumente". Klicken Sie 2x auf **jserver.jar**. Wenn Alles gelaufen ist wie geplant, läuft nun eine funktionierende Kopie von BoS. Öffnen Sie das Code-Fenster und versuchen sie ein Snippet wie:

    farbe(1, 0xff);

zu komplieren. Einer der Kreise im Hauptfenster sollte sich nun rot färben.
