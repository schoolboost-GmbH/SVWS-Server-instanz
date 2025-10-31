### [ValidatorLppbLehrerPersonaldatenPersonalabschnittsdatenBeschaeftigungsart]()

****
**Zweig:** Lehrer-Personaldaten-Personalabschnittsdaten-Beschaeftigungsart <br>
**DTOs:** LehrerPersonalabschnittsdaten <br>
**Ausführungsbereich-UI:** Lehrkräfte, Reiter 'Personaldaten' <br>
**Anzeigebereich-UI:** Feld 'Beschäftigungsart' <br>
**Default-SVWS/ZeBrAS:** <br>
"svws": true, <br>
"zebras": true, <br>
**Default-Fehlerhärte:**<br>
"muss": [], <br>
"kann": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"], <br>
"hinweis": []

---

**Fehlerkürzel:** LPPB2 <br>
**Altes-Fehlerkürzel:** BI7 <br>
**SVWS/ZeBrAS**: Default <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Bei einer unentgeltlich beschäftigten Lehrkraft (Feld 'Beschäftigungsart' = 'Unentgeltlich Beschäftigte') dürfen im Feld 'Einsatzstatus' nicht die Einträge 'Stammschule, ganz oder teilweise auch an anderen Schulen tätig' oder 'nicht Stammschule, aber auch hier tätig' eingetragen sein.<br>
**Erläuterung:** - <br>
**Bedingung:** LehrerPersonaldatenPersonalabschnittsdaten.einsatzstatus = "A", "B" ∧ LehrerPersonaldatenPersonalabschnittsdaten.beschaeftigungsart = "X" <br>

--- 

**Fehlerkürzel:** LPPB3 <br>
**Altes-Fehlerkürzel:** BW15 <br>
**SVWS/ZeBrAS**: Default <br>
**Fehlerhärte**: Default <br>
**Fehlertext:** Laut Ihren Angaben handelt es sich um eine voll abgeordnete Lehrkraft mit Gestellungsvertrag. Es ist zu erwarten, dass eine Lehrkraft mit Gestellungsvertrag Unterricht an Ihrer Schule erteilt. Bitte überprüfen Sie Ihre Angaben. <br>
**Erläuterung:** - <br>
**Bedingung:** LehrerPersonaldatenPersonalabschnittsdaten.beschaeftigungsart = "G" ∧ LehrerPersonaldatenPersonalabschnittsdaten.einsatzstatus = "A" ∧ LehrerPersonaldatenPersonalabschnittsdaten.pflichtstundensoll =  0 <br>
