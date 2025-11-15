# ---- Stage 1: Build Server ----
FROM gradle:8.5-jdk21 AS server-builder
WORKDIR /app

# Copy all server code
COPY . .

WORKDIR /app/svws-server-app
# Build server JAR (skip tests for faster build)
RUN gradle build -x test --no-daemon

# ---- Stage 2: Build Client ----
FROM node:20 AS frontend-client-builder
WORKDIR /app/svws-webclient/client

# Copy client package.json and lockfile first for caching
COPY svws-webclient/client/package*.json ./
COPY ../../tsconfig.base.json ../tsconfig.base.json

# Install devDependencies including vite and plugins
RUN npm install --include=dev --legacy-peer-deps

# Copy the rest of the client source
COPY svws-webclient/client ./

# Build client
RUN npm run build

# ---- Stage 3: Build Admin ----
FROM node:20 AS frontend-admin-builder
WORKDIR /app/svws-webclient/admin

# Copy admin package.json and lockfile first for caching
COPY svws-webclient/admin/package*.json ./
COPY ../../tsconfig.base.json ../tsconfig.base.json

# Install devDependencies including vite and plugins
RUN npm install --include=dev --legacy-peer-deps

# Copy the rest of the admin source
COPY svws-webclient/admin ./

# Build admin
RUN npm run build

# ---- Stage 4: Final Image ----
FROM eclipse-temurin:21

# Install required utilities
RUN apt-get update && apt-get install -y --no-install-recommends \
    gettext-base unzip wget ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/app/svws
RUN mkdir -p client adminclient conf init logs

# Copy server JAR
COPY --from=server-builder /app/svws-server-app/build/libs/*-all-*.jar svws-server-app.jar

# Copy frontend build outputs
COPY --from=frontend-client-builder /app/svws-webclient/client/dist ./client
COPY --from=frontend-admin-builder /app/svws-webclient/admin/dist ./adminclient

# Copy configuration & startup scripts
COPY deployment/docker/svws/svwsconfig-template.json .
COPY deployment/docker/svws/svwsconfig-template-nodb.json .
COPY deployment/docker/svws/startup.sh .

# Make startup script executable
RUN chmod +x startup.sh

ENTRYPOINT ["/bin/bash", "startup.sh"]
