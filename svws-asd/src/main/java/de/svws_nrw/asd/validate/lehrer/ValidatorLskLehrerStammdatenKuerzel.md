### ValidatorLskLehrerStammdatenKuerzel.java

**Default-Fehlerhärte:**<br>
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"kann": [],<br>
"hinweis": []<br>

---

**Fehlerkürzel:** LSK0 <br>
**Altes-Fehlerkürzel:** AD31 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Der Eintrag '" + LehrerStammdaten.Lehrerkürzel + "' ist als Lehrerkürzel unzulässig. Zulässig sind: 1. Stelle: A-Z, Ä, Ö, Ü; 2.-4. Stelle: A-Z, Ä, Ö, Ü, -, 'kein Eintrag'. Buchstaben müssen großgeschrieben werden.  <br>
**Erläuterung:** - <br>
**Bedingung:** <br> 
LehrerStammdaten.Lehrerkürzel 1. Stelle ≠ A-Z,Ä,Ö,Ü (nur Großbuchstaben) <br> 
∧ LehrerStammdaten.Lehrerkürzel 2.-4. Stelle ≠ A-Z,Ä,Ö,Ü,0-9,@,- (nur Großbuchstaben)

---