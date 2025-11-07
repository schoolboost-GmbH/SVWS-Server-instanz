### ValidatorLabbLehrerPersonalabschnittsdatenBeschaeftigungsartBlockmodell.java

**Default-Fehlerhärte:**<br>
"muss": [], <br>
"kann": ["G","H","V","S","KS","R","PS","SK","GE","FW","HI","GY","WB","BK","SR","SG","SB"]<br>
"hinweis": []<br>

---

**Fehlerkürzel:** Labb1 <br>
**Altes-Fehlerkürzel:** BJ3 <br>
**Fehlerhärte:** Default 
**Fehlertext:** Bei einer Lehrkraft mit 'Beschäftigungsart' = TS (Teilzeitbeschäftigung im Blockmodell) muss entweder der Mehrleistungsgrund '100' Ansparphase, Phase mit erhöhter Arbeitszeit "Teilzeitbeschäftigung im Blockmodell" (§ 65 LBG) (vormals Sabbatjahr) oder der Minderleistungsgrund '290' (Ermäßigungs-/Freistellungsphase 'Teilzeitbeschäftigung im Blockmodell') eingetragen sein. <br>
**Erläuterung:** Fachlicher Hinweis 1: Laut Fehler xxx (alt bpe12) muss bei einer langfristigen Erkrankung das Pflichtstundensoll in Gänze über den Grund 240 gemindert werden. Dies ist im Fall von TS vermutlich nicht ganz korrekt. Da BPE12 jedoch für andere Sachverhalte (z.B. Altersermäßigung) ebenfalls nicht ganz korrekt prüft, wird diese Ungenauigkeit hingenommen. 
Fachlicher Hinweis 2: Bei abgeordneten Lehrkräften kann sich bei einer Teilzeitbeschäftigung im Blockmodell ein negatives Pflichtstundensoll ergeben. Ein negatives Pflichtstundensoll soll es jedoch nicht geben. Um diesen Problem zu lösen, kann bei einem Pflichtstundensoll von 0h keine Mehr- oder Minderleistung vorliegen. <br>
**Bedingung:** LehrerPersonalabschnittsdaten.pflichtstundensoll > 0.00 ∧ LehrerPersonalabschnittsdaten.beschaeftigungsart = 'TS' ∧ LehrerPersonalabschnittsdaten.einsatzstatus = @, A ∧ ¬[ LehrerPersonalabschnittsdatenAnrechnungsstunden.idGrund ∈ {100} ∨ LehrerPersonalabschnittsdatenAnrechnungsstunden.idGrund ∈ {240, 290}]
<br>