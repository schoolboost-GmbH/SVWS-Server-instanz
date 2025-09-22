### ValidatorLehrerStammdatenVorname.java

**Default-Fehlerhärte:**<br>
"muss": [],
"kann": [],
"hinweis": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"]

---

**Fehlerkürzel:** LSV0 <br>
**Altes-Fehlerkürzel:** ? <br>
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"kann": [],<br>
"hinweis": []<br>
**Fehlertext:** Kein Wert im Feld 'vorname'. <br>
**Erläuterung:** - <br>
**Bedingung:** <br> LehrerStammdaten.vorname = @ <br>

---

**Fehlerkürzel:** LSV1 <br>
**Altes-Fehlerkürzel:** - <br>
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"kann": [],<br>
"hinweis": []<br>
**Fehlertext:** Vorname der Lehrkraft: Der Vorname darf nicht nur aus Leerzeichen bestehen. <br>
**Erläuterung:** - <br>
**Bedingung:** LehrerStammdaten.vorname = Leerzeichen <br> 

---

**Fehlerkürzel:** LSV2 <br>
**Altes-Fehlerkürzel:** AA5 <br>
"muss": [],<br>
"kann": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"hinweis": []<br>
**Fehlertext:** Vorname der Lehrkraft: Der Vorname besteht aus nur einem Zeichen. Bitte überprüfen sie ihre Angaben. <br>
**Erläuterung:** - <br>
**Bedingung:** LehrerStammdaten.vorname = Einstellig  <br> 

---

**Fehlerkürzel:** LSV3 <br>
**Altes-Fehlerkürzel:** AD33 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Vorname der Lehrkraft: Die Eintragung des Vornamens muss linksbündig erfolgen (ohne vorangestellte Leerzeichen oder Tabs). <br>
**Erläuterung:** - <br>
**Bedingung:** <br> LehrerStammdaten.vorname: 1. Stelle = @ <br>

---

**Fehlerkürzel:** LSV4 <br>
**Altes-Fehlerkürzel:** AD33 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Vorname der Lehrkraft: Die erste Stelle des Vornamens muss mit einem Großbuchstaben besetzt sein. <br>
**Erläuterung:** - <br>
**Bedingung:** <br> LehrerStammdaten.vorname: 1. Stelle = Großbuchstabe <br>

---

**Fehlerkürzel:** LSV5 <br>
**Altes-Fehlerkürzel:** AD331 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Vorname der Lehrkraft: Die zweite Stelle des Vornamens ist mit einem Großbuchstaben besetzt. Bitte stellen sie sicher, dass nur der erste Buchstabe des Vornamens ein Großbuchstabe ist. Bitte schreiben Sie auf ihn folgende Buchstaben klein. <br>
**Erläuterung:** - <br>
**Bedingung:** <br> LehrerStammdaten.vorname: 2. Stelle = Großbuchstabe <br>

---

**Fehlerkürzel:** LSV6 <br>
**Altes-Fehlerkürzel:** AD332 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Vorname der Lehrkraft: Die dritte Stelle des Vornamens ist mit einem Großbuchstaben besetzt. Bitte stellen sie sicher, dass nur der erste Buchstabe des Vornamens ein Großbuchstabe ist. Bitte schreiben Sie auf ihn folgende Buchstaben klein. <br>
**Erläuterung:** - <br>
**Bedingung:** LehrerStammdaten.vorname: 3. Stelle = Großbuchstabe <br>

---

**Fehlerkürzel:** LSV7 <br>
**Altes-Fehlerkürzel:** AA7 <br>
**Fehlerhärte:** <br>
"muss": [], <br>
"kann": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"], <br>
"hinweis": [] <br>
**Fehlertext:** Vorname der Lehrkraft: Der Vorname enthält überflüssige Leerzeichen vor und/oder nach dem Bindestrich. <br>
**Erläuterung:** - <br>
**Bedingung:** " - "  ∈ LehrerStammdaten.vorname ∨ "- " ∈ LehrerStammdaten.vorname ∨ " - " ∈ LehrerStammdaten.vorname <br> 

---

**Fehlerkürzel:** LSV8 <br>
**Altes-Fehlerkürzel:** AA8 <br>
**Fehlerhärte:** <br>
"muss": [], <br>
"kann": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"], <br>
"hinweis": [] <br>
**Fehlertext:** Vorname der Lehrkraft: Die Anrede (Frau oder Herr) gehört nicht in den Vornamen. <br>
**Erläuterung:** - <br>
**Bedingung:**   ("Frau " ∈ LehrerStammdaten.vorname ∧ LehrerStammdaten.vorname ≠ "Frauke") ∨ "Herr " ∈ LehrerStammdaten.vorname <br> 

---