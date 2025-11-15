# ---- Stage 1: Build Server ----
FROM gradle:8.5-jdk21 AS server-builder
WORKDIR /app

# Copy all server code
COPY . .

WORKDIR /app/svws-server-app
# Build server JAR (skip tests to speed up CI/build)
RUN gradle build -x test --no-daemon

# ---- Stage 2: Build Frontend (workspaces) ----
FROM node:20 AS web-builder
WORKDIR /app

# Copy root manifest and workspace package manifests for caching
COPY package*.json ./
COPY tsconfig.base.json ./
COPY svws-webclient/admin/package*.json svws-webclient/admin/
COPY svws-webclient/client/package*.json svws-webclient/client/
COPY svws-webclient/core/package*.json svws-webclient/core/
COPY svws-webclient/enmserver/package*.json svws-webclient/enmserver/
COPY svws-webclient/laufbahnplanung/package*.json svws-webclient/laufbahnplanung/
COPY svws-webclient/ui/package*.json svws-webclient/ui/
COPY tests/tests-server-api/package*.json tests/tests-server-api/

# Install workspace dependencies including devDeps at root and add missing bundlers/plugins
RUN npm ci --include=dev \
    && npm install -D rollup@^4 esbuild typescript vue-tsc @vitejs/plugin-vue unplugin-vue-components unplugin-vue-markdown @tailwindcss/vite tailwindcss @tailwindcss/typography remixicon

# Copy sources
COPY svws-webclient ./svws-webclient
COPY eslint.config.js ./

# Build the client and admin workspaces
RUN npm run -w @svws-nrw/svws-client build \
 && npm run -w @svws-nrw/svws-admin-client build

# ---- Stage 4: Final Image ----
FROM eclipse-temurin:21

# Install required tools
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        gettext-base \
        unzip \
        wget \
        ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/app/svws
RUN mkdir -p client adminclient conf init logs

# Copy server JAR
COPY --from=server-builder /app/svws-server-app/build/libs/*-all-*.jar svws-server-app.jar

# Copy frontend build outputs (Vite outDir is build/output)
COPY --from=web-builder /app/svws-webclient/client/build/output ./client
COPY --from=web-builder /app/svws-webclient/admin/build/output ./adminclient

# Copy config & startup script
COPY deployment/docker/svws/svwsconfig-template.json .
COPY deployment/docker/svws/svwsconfig-template-nodb.json .
COPY deployment/docker/svws/startup.sh .

# Make startup script executable
RUN chmod +x startup.sh

ENTRYPOINT ["/bin/bash", "startup.sh"]
    
