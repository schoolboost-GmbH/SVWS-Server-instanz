### ValidatorLpLehrerPersonaldaten

**Zweig:** Lehrer-Personaldaten <br>
**DTOs:** LehrerPersonaldaten <br>
**Ausführungsbereich-UI:** Lehrkräfte, Reiter 'Personaldaten' <br>
**Anzeigebereich-UI:** Personldaten insgesamt <br>
**Default-SVWS/ZeBrAS:** <br>
"svws": true, <br>
"zebras": true, <br>
Default-Fehlerhärte:
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"kann": [],<br>
"hinweis": []<br>

---

**Fehlerkürzel:** LP0 <br>
**Altes-Fehlerkürzel:** AC3 <br>
**SVWS/ZeBrAS**: Default <br>
**Fehlerhärte:** <br>
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"kann": [],<br>
"hinweis": []<br>
**Fehlertext:** Zu jeder Lehrkraft muss mindestens ein Lehramt vorliegen. <br>
**Erläuterung:** Der Prüfschritt soll anschlagen, wenn zu einer Lehrkraft kein Lehramt erfasst ist. Der Prüfschritt soll nicht für FW, HI und WF prüfen, da hier im Rahmen der ASD kein Lehramt verlangt wird. <br>
**Bedingung:** ANZAHL(LehrerPersonaldaten.lehraemter[]) = 0 <br>

---

