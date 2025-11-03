### ValidatorLehrerPersonalabschnittsdatenPflichtstundensoll.java

****
**Default-Fehlerhärte:**<br>
"muss": [],<br>
"kann": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"hinweis": []

---
**Fehlerkürzel:** LAP0 <br>
**Altes-Fehlerkürzel:** AD51 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Kein Wert im Feld 'pflichtstundensoll'. <br>
**Erläuterung:** Das Feld Pflichtstundensoll muss einen Wert aufweisen. <br>
**Bedingung:** LehrerPersonalabschnittsdaten.pflichtstundensoll = @ <br>

---
**Fehlerkürzel:** LAP1 <br>
**Altes-Fehlerkürzel:** AD51 <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Unzulässiger Wert im Feld 'Pflichtstundensoll'. Zulässig sind im Stundenmodell Werte im Bereich von 0,00 bis 41,00 Wochenstunden. Im Minutenmodell zwischen 0,00 und 1845,00 Minuten. _(Text noch anzupassen)_ <br>
**Bedingung:** LehrerPersonalabschnittsdaten.pflichtstundensoll < 0 ∨ LehrerPersonalabschnittsdaten.pflichtstundensoll > 41.00 <br>

---
**Fehlerkürzel:** LAP2 <br>
**Altes-Fehlerkürzel:** BG <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Bei Lehrkräften, die von einer anderen Schule abgeordnet wurden (Einsatzstatus = 'nicht Stammschule, aber auch hier tätig'), darf das Pflichtstundensoll nicht 0,00 betragen. <br>
**Bedingung:** LehrerPersonalabschnittsdaten.Einsatzstatus = "B" ∧ LehrerPersonalabschnittsdaten.pflichtstundensoll = 0.00

---
**Fehlerkürzel:** LAP3 <br>
**Altes-Fehlerkürzel:** BI6 <br>
**Fehlerhärte:** <br>
"muss": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"kann": [],<br>
"hinweis": []<br>
**Fehlertext:** Ist bei einer Lehrkraft im Feld 'Pflichtstundensoll' der Wert = 0.00 eingetragen, so muss das Feld 'Einsatzstatus' den Schlüssel 'Stammschule, ganz oder teilweise auch an anderen Schulen tätig' oder die 'Beschäftigungsart' den Schlüssel 'Beamte auf Widerruf (LAA) in Vollzeit' bzw. 'Beamte auf Widerruf (LAA) in Teilzeit' aufweisen.<br>
**Erläuterung:** - <br>
**Bedingung:** <br> LehrerPersonalabschnittsdaten.pflichtstundensoll = 0.00 ∧ (LehrerPersonalabschnittsdaten.einsatzstatus ≠ "A" ∧ LehrerPersonalabschnittsdaten.beschaeftigungsart ≠ WV, WT)  

--- 
