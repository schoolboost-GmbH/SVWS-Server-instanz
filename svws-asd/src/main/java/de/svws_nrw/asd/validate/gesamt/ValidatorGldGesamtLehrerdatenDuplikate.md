### ValidatorGldGesamtLehrerdatenDuplikate.java

**Default-Fehlerhärte:**<br>
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"kann": [],<br>
"hinweis": []

---

**Fehlerkürzel:** GLD0 <br>
**Altes-Fehlerkürzel:** - <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Lehrkäfte: Die ID " + LehrerStammdaten.id + " kommt mehrfach vor. <br>
**Erläuterung:** Die Prüfung soll anschlagen, wenn eine Lehrkräfte-ID mehrfach vorkommt. <br>
**Bedingung:** <br> LehrerStammdaten.id(i) = LehrerStammdaten.id(j) <br>i = beliebiger Lehrkräfte-Satz; j = beliebiger Lehrkräfte-Satz; i ≠ j<br>

---

**Fehlerkürzel:** GLD2 <br>
**Altes-Fehlerkürzel:** AA1 <br>
**Fehlerhärte:** <br>
"muss": [],<br>
"kann": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"hinweis": []<br>
**Fehlertext:** Lehrkäfte: Bei den IDs " + LehrerStammdaten.id(i) + " und " + LehrerStammdaten.id(j) + " kommt die Kombination aus Nachname '" + LehrerStammdaten.nachname(i) + "', Vorname '" + LehrerStammdaten.vorname(i) + "', Geburtsdatum '" + LehrerStammdaten.geburtsdatum(i) "' und Geschlecht '" + LehrerStammdaten.geschlecht(i) + "' mehrmals vor. Falls es sich hierbei um eine Person handelt, fassen Sie die Datensätze bitte zusammen.
 <br>
**Erläuterung:** Diese Gesamtprüfung soll anschlagen, wenn es zwei Lehrkräfte-Sätze mit gleichem Nachname, Vorname, Geburtsdatum und Geschlecht gibt. Im Fehlertext sollen die IDs der Lehrkräftesätze sowie Nachname, Vorname, Geburtsdatum und Geschlecht genannt werden. <br>
**Bedingung:** <br>[LehrerStammdaten.nachname(i) = LehrerStammdaten.nachname(j)] ∧ [LehrerStammdaten.vorname(i) = LehrerStammdaten.vorname(j)] ∧ [LehrerStammdaten.geburtsdatum(i) = LehrerStammdaten.geburtsdatum(j)] ∧ [LehrerStammdaten.geschlecht(i) = LehrerStammdaten.geschlecht(j)]<br>
i = beliebiger Lehrkräfte-Satz; j = beliebiger Lehrkräfte-Satz; i ≠ j<br>

---