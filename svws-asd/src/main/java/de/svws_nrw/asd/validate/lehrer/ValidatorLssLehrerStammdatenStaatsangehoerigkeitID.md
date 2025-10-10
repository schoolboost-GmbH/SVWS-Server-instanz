### ValidatorLssLehrerStammdatenStaatsangehoerigkeitID.java

**Default-Fehlerhärte:**<br>
"muss": [],<br>
"kann": ["G", "H", "V", "S", "KS", "R", "PS", "SK", "GE", "FW", "HI", "WF", "GY", "WB", "BK", "SR", "SG", "SB"],<br>
"hinweis": []

---

**Fehlerkürzel:** LSS2 <br>
**Altes-Fehlerkürzel:** BD <br>
**Fehlerhärte:** Default <br>
**Fehlertext:** Zu dieser verbeamteten Lehrkraft ist die Staatsangehörigkeit '" + LehrerStammdaten.staatsangehoerigkeitID + "' angegeben. Dabei handelt es sich jedoch nicht um eine Staatsangehörigkeit eines Mitgliedsstaats der Europäischen Union (EU) oder des Europäischen Wirtschaftsraums (EWR). Die vorgenommene Eintragung kann nur in Ausnahmefällen korrekt sein. Für Lehrkräfte, die neben einer ausländischen Staatsangehörigkeit auch die deutsche Staatsangehörigkeit besitzen, erfassen Sie bitte die Staatsangehörigkeit 'deutsch'. <br>
**Erläuterung:** - <br>
**Bedingung:** <br> LehrerStammdaten.rechtsverhaeltnis = L, N, P, W <br>
∧ LehrerStammdaten.staatsangehoerigkeitID ≠ DEU, BEL, BGR, DNK, EST, FIN, FRA, HRV, SVN, GRC, IRL, ISL, ITA, LVA, LIE, LTU, LUX, MLT, NLD, NOR, AUT, POL, PRT, ROU, SVK, SWE, CHE, ESP, CZE, HUN, GBR, CYP <br>

---