#!/bin/bash

# Ensure conf directory exists
# mkdir -p /opt/app/svws/conf

# Generate configuration file if missing
if [[ ! -s /opt/app/svws/conf/svwsconfig.json ]]; then
    echo "Konfigurationsdatei '/opt/app/svws/svwsconfig.json' nicht vorhanden. Erstelle Konfigurationsdatei..."
    if [ -z "${MARIADB_DATABASE}" ] || [ -z "${MARIADB_USER}" ] || [ -z "${MARIADB_PASSWORD}" ]; then
        echo "Erstelle Konfigurationsdatei ohne Datenbankschema."
        envsubst < /opt/app/svws/svwsconfig-template-nodb.json > /opt/app/svws/svwsconfig.json
    else
        echo "Erstelle Konfigurationsdatei mit dem angegebenen Datenbankschema."
        envsubst < /opt/app/svws/svwsconfig-template.json > /opt/app/svws/svwsconfig.json
    fi
else
    echo "Konfigurationsdatei vorhanden."
fi

# Generate keystore if missing
if [[ ! -s ${SVWS_TLS_KEYSTORE_PATH}/keystore ]]; then
    echo "Keystore '${SVWS_TLS_KEYSTORE_PATH}/keystore' nicht vorhanden. Erstelle Keystore..."
    keytool -genkey -noprompt -alias ${SVWS_TLS_KEY_ALIAS} \
        -dname "CN=$SVWS_TLS_CERT_CN, OU=$SVWS_TLS_CERT_OU, O=$SVWS_TLS_CERT_O, L=$SVWS_TLS_CERT_L, S=$SVWS_TLS_CERT_S, C=$SVWS_TLS_CERT_C" \
        -keystore ${SVWS_TLS_KEYSTORE_PATH}/keystore \
        -storepass ${SVWS_TLS_KEYSTORE_PASSWORD} \
        -keypass ${SVWS_TLS_KEYSTORE_PASSWORD} \
        -keyalg RSA
else
    echo "Keystore vorhanden."
fi

# Start SVWS server using the fat JAR
echo "Starte SVWS-Server ..."
java -cp "../svws-server-app-*.jar:../*:../lib/*" de.svws_nrw.server.jetty.Main
