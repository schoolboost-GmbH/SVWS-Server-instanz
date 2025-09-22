### [ValidatorLehrerStammdatenNachname.java]()

**Default-Fehlerhärte:**<br>
"muss": [],
"kann": [],
"hinweis": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"]

---

**Fehlerkürzel:** LSN0 <br>
**Altes-Fehlerkürzel:** AC2? (muss noch überprüft werden) <br>
**Fehlerhärte:** <br>
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"kann": [],<br>
"hinweis": []<br>
**Fehlertext:** Nachname der Lehrkraft: Kein Wert vorhanden. <br>
**Erläuterung:** - <br>
**Bedingung:** LehrerStammdaten.nachname = @ <br>

**Neues-Fehlerkürzel:** LSN1 <br>
**Altes-Fehlerkürzel:** - <br>
**Fehlerhärte:** <br>
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"kann": [],<br>
"hinweis": []<br>
**Fehlertext:** Nachname der Lehrkraft: Der Nachname darf nicht nur aus Leerzeichen bestehen. <br>
**Erläuterung:** - <br>
**Bedingung:** LehrerStammdaten.nachname = Leerzeichen <br>

---

**Neues-Fehlerkürzel:** LSN2 <br>
**Altes-Fehlerkürzel:** AA5 <br>
**Fehlerhärte:** <br>
"muss": [], <br>
"kann": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"], <br>
"hinweis": [] <br>
**Fehlertext:** Nachname der Lehrkraft: Der Nachname besteht aus nur einem Zeichen. Bitte überprüfen sie Ihre Angaben. <br>
**Erläuterung:** - <br>
**Bedingung:**  LehrerStammdaten.nachname = Einstellig <br>

---

**Fehlerkürzel:** LSN3 <br>
**Altes-Fehlerkürzel:** AD32 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Nachname der Lehrkraft: Die Eintragung des Nachnamens muss linksbündig erfolgen (ohne vorangestellte Leerzeichen oder Tabs). <br>
**Erläuterung:** - <br>
**Bedingung:** LehrerStammdaten.nachname: 1. Stelle = @ <br>

---

**Fehlerkürzel:** LSN4 <br>
**Altes-Fehlerkürzel:** AD32 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Nachname der Lehrkraft: Die erste Stelle des Nachnamens muss - ggf. im Anschluss an einen Namenszusatz, wie z.B. \"von\" -  mit einem Großbuchstaben besetzt sein. <br>
**Erläuterung:** - <br>
**Bedingung:** <br>  LehrerStammdaten.nachname: 1. Stelle ≠ Großbuchstabe <br><br>

---

**Fehlerkürzel:** LSN5 <br>
**Altes-Fehlerkürzel:** AD321 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Nachname der Lehrkraft: Die zweite Stelle des Nachnamens ist mit einem Großbuchstaben besetzt. Bitte stellen sie sicher, dass nur der erste Buchstabe des Nachnamens ein Großbuchstabe ist. Bitte schreiben Sie auf ihn folgende Buchstaben klein. <br>
**Erläuterung:** - <br>
**Bedingung:** <br> LehrerStammdaten.nachname: 1. Stelle = Großbuchstabe ∧ LehrerStammdaten.nachname: 2. Stelle = Großbuchstabe <br>

---

**Fehlerkürzel:** LSN6 <br>
**Altes-Fehlerkürzel:** AD322 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Nachname der Lehrkraft: Die dritte Stelle des Nachnamens ist mit einem Großbuchstaben besetzt. Bitte stellen sie sicher, dass nur der erste Buchstabe des Nachnamens ein Großbuchstabe ist. Bitte schreiben Sie auf ihn folgende Buchstaben klein. <br>
**Erläuterung:** - <br>
**Bedingung:** <br> LehrerStammdaten.nachname: 1. Stelle = Großbuchstabe ∧ LehrerStammdaten.nachname: 3. Stelle = Großbuchstabe <br>

---

**Fehlerkürzel:** LSN7 <br>
**Altes-Fehlerkürzel:** AA7 <br>
**Fehlerhärte:** <br>
"muss": [], <br>
"kann": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"], <br>
"hinweis": [] <br>
**Fehlertext:** Nachname der Lehrkraft: Der Nachname enthält überflüssige Leerzeichen vor und/oder nach dem Bindestrich.
**Erläuterung:** - <br>
**Bedingung:** " - "  ∈ LehrerStammdaten.nachname ∨ "- " ∈ LehrerStammdaten.nachname ∨ " - " ∈ LehrerStammdaten.nachname

---

**Fehlerkürzel:** LSN8 <br>
**Altes-Fehlerkürzel:** AA8 <br>
**Fehlerhärte:** <br>
"muss": [], <br>
"kann": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"], <br>
"hinweis": [] <br>
**Fehlertext:** Nachname der Lehrkraft: Die Anrede (Frau oder Herr) gehört nicht in den Nachnamen.
**Erläuterung:** - <br>
**Bedingung:** "Frau " ∈ LehrerStammdaten.nachname ∨ ("Herr " ∈ LehrerStammdaten.nachname ∧ LehrerStammdaten.nachname ≠ "Herr") <br> 

---