
### ValidatorLehrerPersonalabschnittsdatenRechtsverhaeltnis.java

**Default-Fehlerhärte:**<br>
"muss": [],<br>
"kann": [],<br>
"hinweis": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"]

---

**Fehlerkürzel:** LAR0 <br>
**Altes-Fehlerkürzel:** - <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Kein Wert im Feld 'rechtsverhaeltnis'. <br>
**Erläuterung:** Siehe Fehlertext <br>
**Bedingung:** LehrerPersonalabschnittsdaten.rechtsverhaeltnis = @

---

**Fehlerkürzel:** LAR1 <br>
**Altes-Fehlerkürzel:** BB <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Der Wert für das Geburtsjahr sollte bei Beamten/-innen auf Lebenszeit (Rechtsverhältnis = L)" + " zwischen " + {aktuellesSchuljahr - ?} + " und " + {aktuellesSchuljahr - ?} + " liegen. Bitte prüfen!
 <br>
**Erläuterung:** <br>
**Bedingung:** 
if LehrerStammdaten.geburtsdatum >= 1946:
LehrerPersonalabschnittsdaten.rechtsverhaeltnis = 'L'  <br>
∧ LehrerStammdaten.geburtsdatum (Format: YYYY) < aktuellesSchuljahr - 55  <br>
∧ LehrerStammdaten.geburtsdatum (Format: YYYY) > aktuellesSchuljahr - 20  <br>

---

**Fehlerkürzel:** LAR2 <br>
**Altes-Fehlerkürzel:** BB <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Der Wert für das Geburtsjahr sollte bei Beamten/-innen auf Probe (Rechtsverhältnis = P)" + " zwischen " {aktuellesSchuljahr - 55} " und " {aktuellesSchuljahr - 20} " liegen. Bitte prüfen! <br>
**Erläuterung:** Siehe Fehlertext <br>
**Bedingung:**  LehrerPersonalabschnittsdaten.rechtsverhaeltnis = 'P'  <br>
∧ LehrerStammdaten.geburtsdatum (Format: YYYY) < aktuellesSchuljahr - 55  <br>
∧ LehrerStammdaten.geburtsdatum (Format: YYYY) > aktuellesSchuljahr - 20  <br>

---

**Fehlerkürzel:** LAR3 <br>
**Altes-Fehlerkürzel:** BB <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Der Wert für das Geburtsjahr sollte bei Lehramtsanwärtern/-innen (Rechtsverhältnis = W)" + " zwischen " + {aktuellesSchuljahr - 50} + " und " + {aktuellesSchuljahr - 18} + " liegen. Bitte prüfen!" <br>
**Erläuterung:** Siehe Fehlertext <br>
**Bedingung:**  LehrerPersonalabschnittsdaten.rechtsverhaeltnis = 'W'  <br>
∧ LehrerStammdaten.geburtsdatum (Format: YYYY) < aktuellesSchuljahr - 50  <br>
∧ LehrerStammdaten.geburtsdatum (Format: YYYY) > aktuellesSchuljahr - 18  <br>

---

**Fehlerkürzel:** LAR4 <br>
**Altes-Fehlerkürzel:** BB <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Der Wert für das Geburtsjahr sollte bei sonstigen Rechtsverhältnissen" + " zwischen " + {aktuellesSchuljahr - 80} + " und " + {aktuellesSchuljahr - 18} + " liegen. Bitte prüfen! <br>
**Erläuterung:** Siehe Fehlertext <br>
**Bedingung:**  LehrerPersonalabschnittsdaten.rechtsverhaeltnis ≠ 'L','P','W' <br>
∧ LehrerStammdaten.geburtsdatum (Format: YYYY) < aktuellesSchuljahr - 80  <br>
∧ LehrerStammdaten.geburtsdatum (Format: YYYY) > aktuellesSchuljahr - 18  <br>
