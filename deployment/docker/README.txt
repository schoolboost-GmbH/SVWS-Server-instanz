SVWS Docker Compose Deployment
==============================

1. Erstelle eine `.env` Datei (oder setze die Variablen in deiner Umgebung) und definiere mindestens:
   - `SERVICE_PASSWORD_ROOT`
   - `SERVICE_PASSWORD_DB`
   - optional `SERVICE_DB_USER`, `SVWS_HTTP_PORT`, `SVWS_HTTPS_PORT`, `SVWS_SERVER_IMAGE`
2. Starte die Umgebung mit:
   ```bash
   docker compose -f deployment/docker/docker-compose.svws.yml up -d
   ```
3. Nach dem Start ist der Server über `http://localhost:18080` bzw. `https://localhost:18443` erreichbar (Ports konfigurierbar).

Hinweise:
- Passe die Passwörter/Portwerte vor dem ersten Start an.
- Standard-Host-Ports sind 18080/18443, um Konflikte zu vermeiden. Überschreibe sie bei Bedarf per `SVWS_HTTP_PORT` und `SVWS_HTTPS_PORT`.
- Der Keystore wird beim ersten Start automatisch im Volume `svws-keystore` erzeugt.
- Für ein komplettes Zurücksetzen: `docker compose -f deployment/docker/docker-compose.svws.yml down -v`
