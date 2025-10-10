### ValidatorLplLehrerPersonaldatenLehramt.java

**Default-Fehlerhärte:**<br>
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"kann": [],<br>
"hinweis": []

---

**Fehlerkürzel:** LPL1 <br>
**Altes-Fehlerkürzel:** AC3 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Zu jeder Lehrkraft muss mindestens ein Lehramt vorliegen. <br>
**Erläuterung:** Der Prüfschritt soll anschlagen, wenn zu einer Lehrkraft kein Lehramt erfasst ist. Der Prüfschritt soll nicht für FW, HI und WF prüfen, da hier im Rahmen der ASD kein Lehramt verlangt wird. <br>
**Bedingung:** anzahlLehraemter = 0 <br>

---