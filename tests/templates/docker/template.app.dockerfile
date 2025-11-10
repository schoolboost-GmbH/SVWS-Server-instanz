# Dies ist ein Template File. In den verschiedenen Subpackages wird jeweils ein Dockerfile hieraus abgeleitet.
# Die Datei wird durch das Gradle-Script "buildDockerfile" in das Dockerfile des jeweiligen Subpackages kopiert. Während des Kopiervorgangs
# werden die Platzhalter durch die entsprechenden Werte ersetzt.
FROM GRADLE_PLACEHOLDER_APP_BASE_IMAGE_NAME

COPY build/tempdocker /tmp/build/tempdocker

RUN <<EOF
	mkdir -p /etc/app/svws/conf

	mv /tmp/build/tempdocker/appConf/svws_config.json.template /etc/app/svws/conf/svws_config.json.template
	mv /tmp/build/tempdocker/init-scripts/startup.sh /opt/app/svws/startup.sh
	mv /tmp/build/tempdocker/init-scripts /etc/app/svws/init-scripts

	mkdir /var/ssl

	# Kopiere SSL Zertifikate für die JDK und füge der JDK im Container das Zertifikat hinzu
	openssl req -subj '/CN=GRADLE_PLACEHOLDER_ENM_HOST' -new -newkey rsa:2048 -days 3650 -nodes -x509 -keyout /var/ssl/private.key -out /var/ssl/public.pem
	keytool -importcert -noprompt -keystore /opt/java/openjdk/lib/security/cacerts -alias localhost -file /var/ssl/public.pem
EOF

WORKDIR /opt/app/svws

# Starte die Anwendung mit dem startup Script. Diese wurde auch von Gradle kopiert / generiert und enthält die richtigen Platzhalter
ENTRYPOINT ["/bin/bash", "startup.sh"]
