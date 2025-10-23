### ValidatorSssSchuleStammdatenSchulform

**Zweig:** Schule-Stammdaten-Schulform <br>
**DTOs:** SchuleStammdaten <br>
**Ausführungsbereich-UI:** Schule, Stammdaten der Schule <br>
**Anzeigebereich-UI:** Feld 'Schulform' <br>
**Default-SVWS/ZeBrAS:** <br>
"svws": true, <br>
"zebras": true, <br>
**Default-Fehlerhärte:**<br>
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],
"kann": [],
"hinweis": []

---

**Fehlerkürzel:** SSS0 <br>
**Altes-Fehlerkürzel:** - <br>
**SVWS/ZeBrAS**: Default <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Das Feld 'Schulform' muss besetzt sein. <br>
**Erläuterung:** - <br>
**Bedingung:** SchuleStammdaten.schulform = @ <br>

---

**Fehlerkürzel:** SSS1 <br>
**Altes-Fehlerkürzel:** -<br>
**SVWS/ZeBrAS**: Default <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Das Feld 'Schulform' muss zulässig besetzt sein. <br>
**Erläuterung:** - <br>
**Bedingung:** SchuleStammdaten.schulform ≠ einem Eintrag der Schulform.json  <br>