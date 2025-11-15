# ==== Stage 1: Build Backend ====
FROM gradle:8.5-jdk21 AS server-builder
WORKDIR /app

# Copy entire repo for Gradle build
COPY . .
WORKDIR /app/svws-server-app

# Build server JAR
RUN gradle build -x test --no-daemon


# ==== Stage 2: Build Frontend ====
FROM node:20 AS frontend-builder
WORKDIR /app

# Copy workspace root files
COPY package*.json ./
COPY tsconfig.base.json ./
COPY eslint.config.js ./

# Copy frontend apps + shared code
COPY svws-webclient/client ./svws-webclient/client
COPY svws-webclient/admin ./svws-webclient/admin
COPY svws-webclient/core ./svws-webclient/core
COPY svws-webclient/ui ./svws-webclient/ui
COPY svws-webclient/enmserver ./svws-webclient/enmserver

# Generate githash in both client and admin
ARG GIT_COMMIT=fallback
RUN echo "export const githash = '${GIT_COMMIT}';" > svws-webclient/client/githash.ts \
 && echo "export const githash = '${GIT_COMMIT}';" > svws-webclient/admin/githash.ts

# Install dependencies
RUN npm ci --include=dev

# Install build tools
RUN npm install -D vite@7.2.2 rollup@^4 esbuild typescript vue-tsc \
    @vitejs/plugin-vue unplugin-vue-components unplugin-vue-markdown \
    @tailwindcss/vite tailwindcss @tailwindcss/typography remixicon

# Build frontend apps
RUN npm run -w @svws-nrw/svws-client build
RUN npm run -w @svws-nrw/svws-admin-client build


# ==== Stage 3: Final Runtime Image ====
FROM eclipse-temurin:21
WORKDIR /opt/app/svws

# Create required folders
RUN mkdir -p client adminclient conf init logs

# Copy server JAR
COPY --from=server-builder /app/svws-server-app/build/libs/*-all-*.jar svws-server-app.jar

# Copy frontend build outputs
COPY --from=frontend-builder /app/svws-webclient/client/dist ./client
COPY --from=frontend-builder /app/svws-webclient/admin/dist ./adminclient

# Copy configs and startup script
COPY deployment/docker/svws/svwsconfig-template.json .
COPY deployment/docker/svws/svwsconfig-template-nodb.json .
COPY deployment/docker/svws/startup.sh .
RUN chmod +x startup.sh

# Run server
ENTRYPOINT ["bash","startup.sh"]
