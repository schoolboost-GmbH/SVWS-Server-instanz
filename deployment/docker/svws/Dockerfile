FROM eclipse-temurin:21

RUN apt update && apt-get -y upgrade && apt-get -y install gettext zip && apt autoclean

RUN mkdir -p /opt/app/svws/client && mkdir /opt/app/svws/conf && mkdir /opt/app/svws/init

COPY /svws-server-app /opt/app/svws
COPY /deployment/docker/svws/svwsconfig-template.json /opt/app/svws/svwsconfig-template.json
COPY /deployment/docker/svws/svwsconfig-template-nodb.json /opt/app/svws/svwsconfig-template-nodb.json
COPY /deployment/docker/svws/startup.sh /opt/app/svws/startup.sh
COPY ../svws-webclient/client/build/output /opt/app/svws/client
COPY ../svws-webclient/admin/build/output /opt/app/svws/adminclient
COPY /svws-server-app/build/libs/svws-server-app-all-1.2.0-SNAPSHOT.jar /opt/app/svws/svws-server-app.jar


WORKDIR /opt/app/svws
ENV SVWS_TLS_KEYSTORE_PASSWORD=test123 SVWS_TLS_KEY_ALIAS=alias1 SVWS_TLS_KEYSTORE_PATH=/opt/app/svws/conf SVWS_TLS_CERT_CN=SVWSCERT SVWS_TLS_CERT_OU=School SVWS_TLS_CERT_O=School SVWS_TLS_CERT_L=Duesseldorf SVWS_TLS_CERT_S=NRW SVWS_TLS_CERT_C=Germany

ENTRYPOINT ["/bin/bash", "startup.sh"]