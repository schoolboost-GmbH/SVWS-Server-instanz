### [ValidatorLapeLehrerPersonalabschnittsdatenPflichtstundensollEinsatzstatus.java] ()

**Hauptvalidator:** ValidatorLehrerPersonalabschnittsdatenPflichtstundensoll.java<br>
**Schulformen:** ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "GY", "WB", "BK", "SR", "SG", "SB"]<br>
**Fehlerhärte:** Muss <br>

---

**Fehlerkürzel:** LAPE <br>
**Altes-Fehlerkürzel:** BI6 <br>
**Fehlertext:** Ist bei einer Lehrkraft im Feld 'Pflichtstundensoll' der Wert = 0.00 eingetragen, so muss das Feld 'Einsatzstatus' den Schlüssel 'Stammschule, ganz oder teilweise auch an anderen Schulen tätig' oder die 'Beschäftigungsart' den Schlüssel 'Beamte auf Widerruf (LAA) in Vollzeit' bzw. 'Beamte auf Widerruf (LAA) in Teilzeit' aufweisen.<br>
**Erläuterung:** - <br>
**Bedingung:** <br> [LehrerPersonalabschnittsdaten.pflichtstundensoll](https://git.svws-nrw.de/svws/SVWS-Server/-/blob/dev/svws-asd/src/main/java/de/svws_nrw/asd/data/lehrer/LehrerPersonalabschnittsdaten.java?ref_type=heads) = 0.00 ∧ ([LehrerPersonalabschnittsdaten.einsatzstatus](https://git.svws-nrw.de/svws/SVWS-Server/-/blob/dev/svws-asd/src/main/java/de/svws_nrw/asd/data/lehrer/LehrerPersonalabschnittsdaten.java?ref_type=heads) ≠ "A" ∧ [LehrerPersonalabschnittsdaten.beschaeftigungsart](https://git.svws-nrw.de/svws/SVWS-Server/-/blob/dev/svws-asd/src/main/java/de/svws_nrw/asd/data/lehrer/LehrerPersonalabschnittsdaten.java?ref_type=heads) ≠ WV, WT)  

--- 
