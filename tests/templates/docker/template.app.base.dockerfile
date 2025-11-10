FROM GRADLE_PLACEHOLDER_DOCKER_REG_URL/library/eclipse-temurin:21

COPY build/ /tmp/build

RUN <<EOF
	apt update
	apt-get --no-install-recommends -y install gettext unzip mariadb-client
	apt-get clean
	rm -rf /var/lib/apt/lists/*


	mkdir -p /opt/app/svws/client
	mkdir -p /opt/app/svws/admin

	mv /tmp/build/extLib/* /opt/app/svws/

	# Extrahier die Build Artefakte
	unzip -d /opt/app/svws/client /tmp/build/SVWS-Client.zip
	rm -rf /tmp/build/SVWS-Client.zip
	unzip -d /opt/app/svws/admin /tmp/build/SVWS-Admin-Client.zip
	rm -rf /tmp/build/SVWS-Admin-Client.zip
EOF
