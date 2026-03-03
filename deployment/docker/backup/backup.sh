#!/bin/bash
set +H

# =============================================================================
# RESTORE COMMANDS (run on server)
# =============================================================================
#
# 1. List available backups:
#    docker exec BACKUP_CONTAINER bash -c 'ls -lh /backups/'
#
# 2. Drop the database:
#    docker exec BACKUP_CONTAINER bash -c 'mariadb -h mariadb -uroot -p"$MARIADB_ROOT_PASSWORD" --skip-ssl -e "DROP DATABASE IF EXISTS DATABASE_NAME;"'
#
# 3. Restore from backup:
#    docker exec BACKUP_CONTAINER bash -c 'mariadb -h mariadb -uroot -p"$MARIADB_ROOT_PASSWORD" --skip-ssl < /backups/BACKUP_FILE.sql'
#
# 4. Restart SVWS:
#    docker restart SVWS_CONTAINER_ID
#
# IMPORTANT: Always drop the database first, then restore. Do not restore on
#            top of an existing database.
#
# Find container IDs with: docker ps | grep -E 'backup|svws'
# =============================================================================

BACKUP_HOUR="${BACKUP_HOUR:-3}"

# Calculate seconds until next target hour (e.g. 3 AM)
seconds_until_next_run() {
  NOW=$(date +%s)
  TARGET=$(date -d "today ${BACKUP_HOUR}:00:00" +%s 2>/dev/null || date -d "${BACKUP_HOUR}:00" +%s)
  if [ "$NOW" -ge "$TARGET" ]; then
    TARGET=$(date -d "tomorrow ${BACKUP_HOUR}:00:00" +%s 2>/dev/null || echo $((TARGET + 86400)))
  fi
  echo $((TARGET - NOW))
}

run_backup() {
  DATE=$(date +%Y-%m-%d_%H-%M-%S)

  # Get list of user databases (skip system databases)
  DBS=$(mariadb -h mariadb -uroot -p"$MARIADB_ROOT_PASSWORD" -sNe "SHOW DATABASES;" 2>/dev/null | grep -Ev '^(mysql|information_schema|performance_schema|sys)$' || true)

  if [ -z "$DBS" ]; then
    echo "[$(date)] No user databases found. Skipping backup."
    return
  fi

  echo "[$(date)] Found databases: $(echo $DBS | tr '\n' ' ')"

  # Individual database backups
  while IFS= read -r db; do
    [ -z "$db" ] && continue
    OUT=$(echo "$db" | tr ' ' '_')
    echo "[$(date)] Backing up '$db'..."
    if mariadb-dump -h mariadb -uroot -p"$MARIADB_ROOT_PASSWORD" \
      --single-transaction --routines --triggers --events \
      --databases "$db" --skip-lock-tables > /backups/${OUT}_$DATE.sql 2>/dev/null; then
      echo "[$(date)]   -> ${OUT}_$DATE.sql ($(du -h /backups/${OUT}_$DATE.sql | cut -f1))"
    else
      echo "[$(date)]   -> FAILED to backup '$db'"
      rm -f /backups/${OUT}_$DATE.sql
    fi
    sleep 2
  done <<< "$DBS"

  # Remove backups older than retention period
  RETENTION_DAYS="${BACKUP_RETENTION_DAYS:-7}"
  echo "[$(date)] Removing backups older than ${RETENTION_DAYS} days..."
  find /backups -name "*.sql" -type f -mtime +${RETENTION_DAYS} -delete

  echo "[$(date)] Backup complete. Current backups:"
  ls -lh /backups/*.sql 2>/dev/null || echo "  (none)"
}

# Run one backup immediately on startup
echo "[$(date)] Running initial backup on startup..."
run_backup

# Then wait for 3 AM each day
while true; do
  WAIT=$(seconds_until_next_run)
  NEXT=$(date -d "+${WAIT} seconds" 2>/dev/null || date -r $(($(date +%s) + WAIT)))
  echo "[$(date)] Next backup at ${BACKUP_HOUR}:00 (in ${WAIT}s, approx ${NEXT})"
  sleep "$WAIT"
  echo "[$(date)] Running scheduled 3 AM backup..."
  run_backup
done
