# ---- Stage 1: Build Server ----
FROM gradle:8.5-jdk21 AS server-builder
WORKDIR /app

# Copy all server code (multi-module build)
COPY . .

WORKDIR /app/svws-server-app
# Build server JAR (skip tests to speed up container builds)
RUN gradle build -x test --no-daemon

# ---- Stage 2: Build Frontend (workspaces) ----
FROM node:20 AS web-builder
WORKDIR /app

# Copy root manifest and package manifests for better caching
COPY package*.json ./
COPY tsconfig.base.json ./
COPY svws-webclient/admin/package*.json svws-webclient/admin/
COPY svws-webclient/client/package*.json svws-webclient/client/
COPY svws-webclient/core/package*.json svws-webclient/core/
COPY svws-webclient/enmserver/package*.json svws-webclient/enmserver/
COPY svws-webclient/laufbahnplanung/package*.json svws-webclient/laufbahnplanung/
COPY svws-webclient/ui/package*.json svws-webclient/ui/
COPY tests/tests-server-api/package*.json tests/tests-server-api/
COPY svws-asd/package*.json svws-asd/

# Install dependencies and the tooling Vite needs during the build
RUN npm ci --include=dev \
    && npm install -D vite@7.2.2 rollup@^4 esbuild typescript vue-tsc @vitejs/plugin-vue unplugin-vue-components unplugin-vue-markdown @tailwindcss/vite tailwindcss @tailwindcss/typography remixicon

# Copy sources required for builds
COPY svws-webclient ./svws-webclient
COPY svws-asd ./svws-asd
COPY eslint.config.js ./

# Ensure generated version.ts and githash.ts exist (normally produced by Gradle)
RUN node - <<'EOF'
const fs = require('fs');
const path = require('path');
const version = require('./package.json').version || '0.0.0';
for (const pkg of ['client', 'admin', 'laufbahnplanung', 'enmserver']) {
  const dir = path.join('svws-webclient', pkg);
  fs.mkdirSync(dir, { recursive: true });
  fs.writeFileSync(path.join(dir, 'version.ts'), `export const version = "${version}"\n`);
  fs.writeFileSync(path.join(dir, 'githash.ts'), 'export const githash = null\n');
}
EOF

# Build the client and admin workspaces (Vite outDir = build/output)
RUN npm run -w @svws-nrw/svws-client build \
 && npm run -w @svws-nrw/svws-admin-client build

# ---- Stage 3: Prebuilt Frontend Artifacts (optional) ----
FROM scratch AS prebuilt-frontend
COPY deployment/docker/frontend-dist/client /app/svws-webclient/client/build/output
COPY deployment/docker/frontend-dist/admin /app/svws-webclient/admin/build/output

# ---- Stage 4: Final Image ----
FROM eclipse-temurin:21
ARG FRONTEND_SOURCE=web-builder

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

# Copy frontend build outputs (source depends on FRONTEND_SOURCE arg)
COPY --from=${FRONTEND_SOURCE} /app/svws-webclient/client/build/output ./client
COPY --from=${FRONTEND_SOURCE} /app/svws-webclient/admin/build/output ./adminclient

# Copy config & startup script
COPY deployment/docker/svws/svwsconfig-template.json .
COPY deployment/docker/svws/svwsconfig-template-nodb.json .
COPY deployment/docker/svws/startup.sh .

# Make startup script executable
RUN chmod +x startup.sh

ENTRYPOINT ["/bin/bash", "startup.sh"]
