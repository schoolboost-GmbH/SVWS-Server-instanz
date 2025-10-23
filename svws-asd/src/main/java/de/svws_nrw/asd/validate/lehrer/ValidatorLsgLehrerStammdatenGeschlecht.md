### ValidatorLsgLehrerStammdatenGeschlecht

**Zweig:** Lehrer-Stammdaten-Geschlecht <br>
**DTOs:** LehrerStammdaten <br>
**Ausführungsbereich-UI:** Lehrkräfte, Reiter 'Individualdaten' <br>
**Anzeigebereich-UI:** Feld 'Geschlecht' <br>
**Default-SVWS/ZeBrAS:** <br>
"svws": true, <br>
"zebras": true, <br>
**Default-Fehlerhärte:**<br>
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"kann": [],<br>
"hinweis": []

---

**Fehlerkürzel:** LSG0 <br>
**Altes-Fehlerkürzel:** AD344 <br>
**SVWS/ZeBrAS**: Default <br>
**Fehlerhärte:** default<br>
**Fehlertext:** Unzulässiger Schlüssel '" + LehrerStammdaten.geschlecht + "' im Feld 'Geschlecht'. Die gültigen Schlüssel entnehmen Sie bitte dem Pulldownmenü. <br>
**Erläuterung:** Der Prüfschritt soll anschlagen, wenn für das Feld Geschlecht ein unzulässiger Wert vorliegt. Die zulässigen Werte finden sich in der Klasse "Geschlecht.java". <br>
**Bedingung:** - <br> 

---