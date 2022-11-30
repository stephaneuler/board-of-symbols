# ImageTaggingSystem
Das ImageTaggingSystem ist eine Anwendung, die die Verschlagwortung von Bildern für eine Bilderdatenbank vereinfacht. Hierfür werden auf Basis der Objekterkennung, Klassifikation, Texterkennung und ähnlichen Bildern aus dem bereits existierenden Datenbestand geeignete Wörter vorgeschlagen. Das System ist dabei auf den Datenbestand des [DFWS](https://www.weinetikettensammler.de/) ausgerichtet.

Die Objekterkennung unterstützt die Motive:
- **Trauben**
- **Katzen**
- **Schiffe**
- **Teufel**

Die Klassifikation unterstützt die Stilrichtungen:
- **Comic**
- **Jugendstil**
- **Kinderzeichnung**

Die Texterkennung sucht Städte und Länder, so wie benutzerdefinierte Schlagwörter raus. Letztere können individuell festgelegt werden, wobei aktuell folgende berücksichtigt werden:
- **Motive** (Katzen, Teufel, Schiffe)
- **Weinarten** (Markenwein, Messwein, Perlwein, Punsch, Sekt, Sonderfall: Musteretikett)
- **Weindetails** (Burgunder, Chardonnay, Chianti, Landwein, Moselwein, Rheinwein, Riesling, Rotwein, Sherry, Silvaner, Süßwein, Tafelwein, Vin de Pays, Weißwein)
- **Marken** (Liebfraumilch)
- **Französische Weinbaugebiete** (Cotes de Provence, Cotes du Rhone, Cotes du Ventoux)
- **Besondere Schlüsselwörter**, z.B. deutsche Weinbaugebiete, besondere Städte etc. (Malvoisie, Thurgau, Waadt, Ahr, Aubonne, Ausland, Beaujolais, Beaujolais Nouveau, Beaujolais-Villages, Bonvillars, Burgund, Deidesheim, Dézaley, Epesses, Franken, Féchy, Hessische Bergstraße, Langenlois, Lonay, Mittelrhein, Morges, Mosel, Nahe, Napa Valley, Pfalz, Rheingau, Rheinhessen, Saale-Unstrut, Sachsen, Wien, Württemberg)

## Funktionen
Es gibt insgesamt 3 verschiedene Ansichten, die unterschiedliche Funktionen bereitstellen.
- Seite zur Testung bzw. Gegenüberstellung verschiedener Ansätze
- Seite zur Auswertung eines Bildes mit allen vier Komponenten
- Seite für Einstellungen von Schwellwerten und Schlagwörtern


## Voraussetzungen
Notwendige Voraussetzungen sind:
- Python: 3.9
- Angular CLI: 13.2.0
- Node: 16.13.2
- Package Manager: npm 8.1.2

## Extern verwendeter Code
Für die Objekterkennung wird sowohl für den Trainingsprozess als auch für die Bildauswertung die [**Object Detection API von TensorFlow**](https://github.com/tensorflow/models/tree/master/research/object_detection) eingesetzt. In diesem Zusammenhang sind auch externe Modelle des [**Detection Model Zoos**](https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf2_detection_zoo.md) als Basis oder zum Vergleich herangezogen worden, ebenso wie ihre Label-Map. Teilausschnitte des Codes der API werden auch integriert, um Bilder in Bezug auf Motive auszuwerten und diese einzuzeichnen. Die dafür notwendigen Dateien befinden sich im Ordner "imagetaggingsystem/server/extern_object_detection/". Des Weiteren wurde für den Ansatz der Textextraktion mit EAST ein vorhandenes Modell namens [**frozen_east_text_detection.pb**](https://github.com/oyyd/frozen_east_text_detection.pb)  und ein zugehöriges [**Codebeispiel**](https://pyimagesearch.com/2018/08/20/opencv-text-detection-east-text-detector/) integriert.

## Setup
### Notwendige Schritte
1. **Datenbank aufsetzen und anbinden**
	- Das Skript **example_database_values.sql** muss ausgeführt werden, um die Datenbank und ihre Inhalte aufzubauen. Anstelle des umfangreichen Datenbestands von 12.069 Bildern wurde hierbei der Bestand auf 5 Bilder beschränkt.
	- In der Datei **controller.py** (in imagetaggingsystem/server) werden in den Zeilen 49-53 Angaben für die Datenbank definiert. Diese müssen ggf. angepasst werden.<br /><br />
2. **Server aufsetzen**
	- Den Pfad "imagetaggingsystem/server" bspw. mit PyCharm öffnen und die angegebenen Bibliotheken in der **requirements.txt** installieren.
	- In der Datei **setup.py** den Methodenaufruf **download_nltk_modul()** ausführen, um die Daten für den Locationtagger der Texterkennung zu installieren
	- Die folgenden Konsolenbefehle im Terminal ausführen:
		- **python -m spacy download en_core_web_sm**
		- **python -m spacy download de_core_news_sm**
	- Beim erstmaligen Serverstart (durch das Ausführen der Datei controller.py) werden automatisch die **Gewichte des VGG16-Modells** gedownloaded, die bei späteren Starts immer wieder verwendet werden.
	- **Hinweis:** Falls die Fehlermeldung "**ImportError:** Cannot import name 'builder' from 'google.protobuf.internal' (...)" auftritt, muss die 'builder.py'-Datei manuell in die Libraries integriert werden. Anleitung dazu gibt es [hier](https://stackoverflow.com/questions/71759248/importerror-cannot-import-name-builder-from-google-protobuf-internal).<br /><br />
3. **Client aufsetzen**
	- Den Pfad "imagetaggingsystem/client" bspw. mit Visual Studio Code öffnen und mit dem Befehl **npm i** die Bibliotheken installieren.

### Optionale Schritte
Die folgenden Schritte sind optional, da sie nur ggf. bei der Seite zur Testung verschiedener Ansätze eingesetzt bzw. angeboten werden. Auch ohne diese Schritte ist die Hauptseite mit der vollständigen Bildauswertung und die Einstellungsseite voll funktionsfähig.

1. **Objekterkennung**
Es wurden für die Testreihen auch die Modelle "EfficientDet D0 512x512" und "EfficientDet D7 1536x1536" ausgewertet. Diese Modelle müssen auf Wunsch manuell von [**Detection Model Zoo**](https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf2_detection_zoo.md) heruntergeladen werden. Diese werden anschließend unter "imagetaggingsystem/server/intern_object_detection/other_models" platziert. Abweichungen des Pfades oder des Namens müssen ggf. in **path_helper.py** unter "imagetaggingsystem/server/helper/" in den Zeilen 15 bzw. 16 angepasst werden. <br/><br/>
2. **Texterkennung mit Tesseract**
Neben pytesseract muss auch Tesseract selbst auf dem Rechner installiert werden. Für Windows wird eine [**setup-Datei**](https://github.com/UB-Mannheim/tesseract/wiki) zum Downloaden angeboten. Der Dateipfad zur tesseract.exe muss im Code angegeben werden. Ggf. muss dieser im **path_helper.py** unter "imagetaggingsystem/server/helper/" in Zeile 34 angepasst werden. Darüber hinaus muss für den Ansatz der Textextraktion das externe **frozen-east-Modell** integriert werden. Dieses kann u.a. [hier](https://github.com/oyyd/frozen_east_text_detection.pb) gefunden werden.


## Anwendung starten
1. **Server starten**
Hierfür **controller.py** im Pfad "imagetaggingsystem/server" ausführen. Dabei wird mit Hilfe von Flask ein Server unter http://localhost:5000 gestartet. Der Server benötigt zum Start eine gewisse Zeit, da verschiedene Models im Vorfeld geladen werden, um kommende Anfragen schneller bearbeiten zu können. Wenn eine andere Adresse verwendet werden soll, muss diese an zwei Punkten angepasst werden:<br/>
	- In controller.py in Zeile 584
	- Im Client  in Zeile 12 der "app.component.ts"-Datei (in imagetaggingsystem/client/src/app)

2. **Client starten**
In der Konsole mit client-Ordner navigieren (imagetaggingsystem/client) und
dort den Befehl `ng serve --open` eingeben. Dabei wird der Client unter http://localhost:4200 gestartet und direkt in einem Browser Fenster geöffnet.

3. Wenn Server und Client gestartet sind, können von Seiten des Clients Anfragen an den Server gestellt werden. Falls der Client vor dem Server gestartet wird, wird die Webseite eine Fehlermeldung ausgeben. Sobald der Server erreichbar ist, kann die Webseite neu geladen werden, um Zugriff zu den Funktionen zu erhalten.

## Datenbestand anpassen
Der Datenbestand wurde für ein erleichtertes Ausführen auf 5 Bilder reduziert. Um mehr Bilder zu integrieren, können diese in Unterordnern in **"imagetaggingsystem/client/src/assets/images"** eingefügt werden. Des Weiteren sollten entsprechende Einträge in der **SQL-Datenbank** ergänzt werden. Um auch die Ansätze zur Ermittlung ähnlicher Bilder darauf auszurichten, muss in der **setup.py** im Pfad "imagetaggingsystem/server" die Methode **setup_similarity()** ausgeführt werden. Wenn nur die Gesamtevaluation eines Bildes benötigt wird, reicht dabei die Ausführung "setup_characteristics()".
