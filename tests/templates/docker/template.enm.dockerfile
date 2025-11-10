FROM GRADLE_PLACEHOLDER_DOCKER_REG_URL/library/php:8.4-apache
# Aktivieren des Rewrite und SSL -Moduls
RUN <<EOF
	a2enmod ssl rewrite
	a2ensite default-ssl
	a2enmod rewrite
EOF

# Baue selbst signierte SSL Zertifikat
COPY ./build/tempdocker/enm/certs/private.key /etc/ssl/private/ssl-cert-snakeoil.key
COPY ./build/tempdocker/enm/certs/public.pem  /etc/ssl/certs/ssl-cert-snakeoil.pem
COPY ./build/tempdocker/enm/hostconfig/enm.apache-config.conf /etc/apache2/sites-available/000-default.conf
COPY ./build/tempdocker/enm/hostconfig/enm.apache-config-ssl.conf /etc/apache2/sites-available/default-ssl.conf
COPY --chown=www-data:www-data ./build/tempdocker/enm/dist /var/www/html

WORKDIR /var/www/html
