FROM eclipse-temurin:21

RUN apt update && apt-get -y upgrade && apt-get -y install gettext zip && apt autoclean
RUN mkdir -p /opt/app/svws/
# RUN mkdir -p /opt/app/svws/client && mkdir /opt/app/svws/conf && mkdir /opt/app/svws/init

# COPY svws-server-app/build/libs/svws-server-app-all-1.2.0-SNAPSHOT.jar /opt/app/svws
COPY deployment/docker/svws/svwsconfig-template.json /opt/app/svws/svwsconfig-template.json
COPY deployment/docker/svws/svwsconfig-template-nodb.json /opt/app/svws/svwsconfig-template-nodb.json
COPY deployment/docker/svws/startup.sh /opt/app/svws/startup.sh

# RUN unzip -d /opt/app/svws/client /opt/app/svws/SVWS-Client*.zip && rm -rf /opt/app/svws/SVWS-Client*.zip && unzip -d /opt/app/svws/adminclient /opt/app/svws/SVWS-Admin-Client*.zip && rm -rf /opt/app/svws/SVWS-Admin-Client*.zip

WORKDIR /opt/app/svws
ENV SVWS_TLS_KEYSTORE_PASSWORD=test123 SVWS_TLS_KEY_ALIAS=alias1 SVWS_TLS_KEYSTORE_PATH=/opt/app/svws/conf SVWS_TLS_CERT_CN=SVWSCERT SVWS_TLS_CERT_OU=School SVWS_TLS_CERT_O=School SVWS_TLS_CERT_L=Duesseldorf SVWS_TLS_CERT_S=NRW SVWS_TLS_CERT_C=Germany
EXPOSE 3000
ENTRYPOINT ["/bin/bash", "startup.sh"]
