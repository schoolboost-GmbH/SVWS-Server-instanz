# ---- Build Stage ----
    FROM gradle:8.5-jdk21 AS builder
    WORKDIR /app
    
    COPY . .
    
    WORKDIR /app/svws-server-app
    RUN gradle build --no-daemon
    
    # ---- Final Image ----
    FROM eclipse-temurin:21
    
    RUN apt update && apt-get -y install gettext zip && apt autoclean
    
    WORKDIR /opt/app/svws
    RUN mkdir -p client adminclient conf init logs
    
    COPY --from=builder /app/svws-server-app/build/libs/*-all-*.jar svws-server-app.jar
    
    COPY deployment/docker/svws/svwsconfig-template.json .
    COPY deployment/docker/svws/svwsconfig-template-nodb.json .
    COPY deployment/docker/svws/startup.sh .
    
    COPY svws-webclient/client/build/output client
    COPY svws-webclient/admin/build/output adminclient
    
    ENTRYPOINT ["bash", "startup.sh"]
    