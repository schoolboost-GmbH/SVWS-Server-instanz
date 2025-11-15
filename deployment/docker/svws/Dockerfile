# ---- Stage 1: Build Server ----
FROM gradle:8.5-jdk21 AS server-builder
WORKDIR /app

# Copy all server code
COPY . .

WORKDIR /app/svws-server-app
RUN gradle build -x test --no-daemon

# ---- Stage 2: Build Frontend ----
FROM node:20 AS frontend-builder
WORKDIR /app

COPY package*.json ./
COPY tsconfig.base.json ./
COPY svws-webclient ./svws-webclient
COPY svws-asd ./svws-asd
COPY eslint.config.js ./

RUN npm ci --include=dev \
    && npm install -D vite@7.2.2 rollup@^4 esbuild typescript vue-tsc \
        @vitejs/plugin-vue unplugin-vue-components unplugin-vue-markdown \
        @tailwindcss/vite tailwindcss @tailwindcss/typography remixicon


RUN npm run -w @svws-nrw/svws-client build \
 && npm run -w @svws-nrw/svws-admin-client build

# ---- Stage 3: Final Image ----
FROM eclipse-temurin:21

RUN apt-get update \
    && apt-get install -y --no-install-recommends gettext-base unzip wget ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/app/svws
RUN mkdir -p client adminclient conf init logs

COPY --from=server-builder /app/svws-server-app/build/libs/*-all-*.jar svws-server-app.jar
COPY --from=frontend-builder /app/svws-webclient/client/build/output ./client
COPY --from=frontend-builder /app/svws-webclient/admin/build/output ./adminclient

COPY deployment/docker/svws/svwsconfig-template.json .
COPY deployment/docker/svws/svwsconfig-template-nodb.json .
COPY deployment/docker/svws/startup.sh .
RUN chmod +x startup.sh

ENTRYPOINT ["/bin/bash", "startup.sh"]
