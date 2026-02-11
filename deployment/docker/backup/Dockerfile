FROM mariadb:11.7

# Copy your backup script into the container
COPY deployment/docker/backup/backup.sh .
RUN chmod +x backup.sh

# Run the backup script
ENTRYPOINT ["/bin/sh", "backup.sh"]
