#!/bin/bash
set -euo pipefail

TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")
BACKUP_DIR="/backups"
RETENTION_DAYS="${BACKUP_RETENTION_DAYS:-7}"
SKIP_DATABASES="information_schema|performance_schema|sys|mysql"

# Get list of all user databases
DATABASES=$(mariadb \
  --host="${MARIADB_HOST}" \
  --user="${MARIADB_USER}" \
  --password="${MARIADB_PASSWORD}" \
  --skip-column-names \
  -e "SHOW DATABASES;" | grep -Ev "^(${SKIP_DATABASES})$")

echo "[$(date)] Found databases: $(echo ${DATABASES} | tr '\n' ' ')"

FAILED=0
for DB in ${DATABASES}; do
  BACKUP_FILE="${BACKUP_DIR}/${DB}_${TIMESTAMP}.sql.gz"
  echo "[$(date)] Backing up '${DB}'..."

  if mariadb-dump \
    --host="${MARIADB_HOST}" \
    --user="${MARIADB_USER}" \
    --password="${MARIADB_PASSWORD}" \
    --single-transaction \
    --routines \
    --triggers \
    --events \
    "${DB}" | gzip > "${BACKUP_FILE}"; then
    echo "[$(date)]   -> ${BACKUP_FILE} ($(du -h "${BACKUP_FILE}" | cut -f1))"
  else
    echo "[$(date)]   -> FAILED to backup '${DB}'"
    rm -f "${BACKUP_FILE}"
    FAILED=$((FAILED + 1))
  fi
done

# Remove backups older than retention period
echo "[$(date)] Removing backups older than ${RETENTION_DAYS} days..."
find "${BACKUP_DIR}" -name "*.sql.gz" -type f -mtime +${RETENTION_DAYS} -delete

echo "[$(date)] Backup complete. Current backups:"
ls -lh "${BACKUP_DIR}"/*.sql.gz 2>/dev/null || echo "  (none)"

if [ ${FAILED} -gt 0 ]; then
  echo "[$(date)] WARNING: ${FAILED} database(s) failed to backup!"
  exit 1
fi
