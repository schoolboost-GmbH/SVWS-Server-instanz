### ValidatorLplLehrerPersonaldatenLehramt

**Zweig:** Lehrer-Personaldaten-Lehramt <br>
**DTOs:** LehrerPersonaldaten, LehrerStammdaten <br>
**Ausführungsbereich-UI:** Lehrkräfte, Reiter 'Personaldaten' <br>
**Anzeigebereich-UI:** Lehrämter (an der betroffenen Zeile) <br>
**Default-SVWS/ZeBrAS:** <br>
"svws": true, <br>
"zebras": true, <br>
**Default-Fehlerhärte:**<br>
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"kann": [],<br>
"hinweis": []<br>

---

**Fehlerkürzel:** LPL1 <br>
**Altes-Fehlerkürzel:** AC3 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Zu jeder Lehrkraft muss mindestens ein Lehramt vorliegen. <br>
**Erläuterung:** Der Prüfschritt soll anschlagen, wenn zu einer Lehrkraft kein Lehramt erfasst ist. Der Prüfschritt soll nicht für FW, HI und WF prüfen, da hier im Rahmen der ASD kein Lehramt verlangt wird. <br>
**Bedingung:** anzahlLehraemter = 0 <br>

---

**Fehlerkürzel:** LPL2 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Das Lehramt '" + LehrerPersonaldaten.lehraemter.text + "' ist mehrfach eingetragen. Bitte löschen Sie die überflüssigen Einträge. <br>
**Bedingung:** <br> LehrerPersonaldaten.lehraemter[](i) = LehrerPersonaldaten.lehraemter[](j) <br>
i = beliebiger Lehramtseintrag; j = beliebiger Lehramtseintrag, i ≠ j

---

**Fehlerkürzel:** LPL3 <br>
**Altes-Fehlerkürzel:** BK6 <br>
**Fehlerhärte:** <br> 
"muss": [], <br>
"kann": [], <br>
"hinweis": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"] <br>
**Fehlertext:** Für das Lehramt '" + LehrerPersonaldaten.lehraemter.text + "' ist die Lehrkraft sehr jung. Wenn das Alter der Lehrkraft korrekt ist, sollte das eingetragene Lehramt überprüft werden. Bitte verwenden Sie die 'regulären' Lehrämter nur dann, wenn eine entsprechende abgeschlossene Ausbildung vorliegt. Wenn es sich um einen Studierenden handelt, der neben seinem Studium als Lehrkraft tätig ist, verwenden sie bitte das Lehramt 'Studierende'. Ansonsten tragen Sie bitte das Lehramt 'Sonstiges' ein. <br>
**Erläuterung:** - <br>
**Bedingung:** LehrerStammdaten.geburtsdatum[YYYY] = 2003 bis 2006 ∧ LehrerPersonaldaten.lehraemter[] = 'ID_00', 'ID_01', 'ID_02', 'ID_03', 'ID_04', 'ID_08', 'ID_09', 'ID_10', 'ID_11', 'ID_12', 'ID_14', 'ID_15', 'ID_16', 'ID_17', 'ID_19', 'ID_20', 'ID_21', 'ID_24', 'ID_25', 'ID_27', 'ID_29', 'ID_30', 'ID_31', 'ID_32', 'ID_35', 'ID_40', 'ID_50', 'ID_51', 'ID_52', 'ID_53', 'ID_54', 'ID_55', 'ID_96' <br>

---