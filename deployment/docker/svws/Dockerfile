# ---- Build Stage (Server) ----
    FROM gradle:8.5-jdk21 AS builder
    WORKDIR /app
    
    # Copy all server code
    COPY . .
    
    WORKDIR /app/svws-server-app
    # Build server JAR
    RUN gradle build --no-daemon
    
    # ---- Frontend Build Stage ----
    FROM node:20 AS frontend-builder
    WORKDIR /app/svws-webclient
    
    # Copy frontend source code
    COPY svws-webclient/client ./client
    COPY svws-webclient/admin ./admin
    # Copy base tsconfig if referenced
    COPY tsconfig.base.json ./tsconfig.base.json
    
    # Optionally, skip frontend build and just copy existing output
    # Uncomment the following lines if you have prebuilt frontend
    # COPY svws-webclient/client/build/output ./client/build/output
    # COPY svws-webclient/admin/build/output ./admin/build/output
    
    # Build client
    WORKDIR /app/svws-webclient/client
    RUN npm install
    RUN npx vite build 
    
    # Build admin
    WORKDIR /app/svws-webclient/admin
    RUN npm install
    RUN npx vite build 
    
    # ---- Final Image ----
    FROM eclipse-temurin:21
    
    RUN apt update && apt-get -y install gettext zip && apt autoclean
    
    WORKDIR /opt/app/svws
    RUN mkdir -p client adminclient conf init logs
    
    # Copy server JAR
    COPY --from=builder /app/svws-server-app/build/libs/*-all-*.jar svws-server-app.jar
    
    # Copy config & startup script
    COPY deployment/docker/svws/svwsconfig-template.json .
    COPY deployment/docker/svws/svwsconfig-template-nodb.json .
    COPY deployment/docker/svws/startup.sh .
    
    # Copy frontend build output from frontend-builder
    COPY --from=frontend-builder /app/svws-webclient/client/build/output client
    COPY --from=frontend-builder /app/svws-webclient/admin/build/output adminclient
    
    # Make startup script executable
    RUN chmod +x startup.sh
    
    ENTRYPOINT ["bash", "startup.sh"]
    
