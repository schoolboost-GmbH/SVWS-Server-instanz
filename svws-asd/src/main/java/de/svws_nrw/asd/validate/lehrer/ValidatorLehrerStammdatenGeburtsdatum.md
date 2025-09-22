### ValidatorLehrerStammdatenGeburtsdatum.java

**Default-Fehlerhärte:**<br>
"muss": [],
"kann": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],
"hinweis": []

---

**Fehlerkürzel:** LSG0 <br>
**Altes-Fehlerkürzel:** <br>
**Fehlerhärte:** <br>
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"kann": [],<br>
"hinweis": []<br>
**Fehlertext:** Das Geburtsdatum ist ungültig: <br>
**Erläuterung:** - <br>
**Bedingung:** (Im Java Code wird das Geburtsdatum zur Prüfung an den DateManager übergeben. Deswegen können wir die genaue Bedingung dazu nicht spezifizieren)

---

**Fehlerkürzel:** LSG1 <br>
**Altes-Fehlerkürzel:** AD343 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Unzulässige Eintragung im Feld Jahr (Geburtsdatum). Zulässig sind die Werte {aktuellesSchuljahr} - 80 bis {aktuellesSchuljahr} - 18}. <br>
**Erläuterung:** Die zulässigen Werte liegen zwischen dem aktuellen Schuljahr minus 80 und dem aktuellen Schuljahr minus 18. <br>
**Bedingung:** ¬ LehrerStammdaten.geburtsdatum (format: JJJJ) > aktuellesSchuljahr - 80 ∧ LehrerStammdaten.geburtsdatum (format: JJJJ) < aktuellesSchuljahr - 18 <br>

---