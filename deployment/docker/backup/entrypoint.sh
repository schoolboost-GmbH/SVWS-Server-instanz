#!/bin/bash
set -euo pipefail

# Write environment variables to a file so cron can access them
printenv | grep -E '^(MARIADB_|BACKUP_)' > /etc/environment.conf

# Build the cron job with the schedule from env (default: 3 AM daily)
CRON_SCHEDULE="${BACKUP_CRON_SCHEDULE:-0 3 * * *}"

cat > /etc/cron.d/db-backup <<EOF
${CRON_SCHEDULE} root . /etc/environment.conf && /usr/local/bin/backup.sh >> /var/log/backup.log 2>&1
EOF

chmod 0644 /etc/cron.d/db-backup

# Ensure log file exists
touch /var/log/backup.log

echo "[$(date)] Backup scheduler started (schedule: ${CRON_SCHEDULE})"
echo "[$(date)] Waiting for first scheduled run..."

# Start cron in foreground and tail the log
cron
exec tail -f /var/log/backup.log
