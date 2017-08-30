#!/bin/bash
# run.sh

set -e

url="http://security:8080/security/resources/health"

function _shutdown() {
    echo "Received TERM signal, shutting down now..."
    /opt/wildfly/bin/jboss-cli.sh -c ":shutdown(timeout=3)"
    wait $!
    exit 0
}

trap _shutdown SIGTERM

echo "Checking $url before starting Wildfly..."

until curl -fs "$url"; do
    >&2 echo "$url is unavailable - sleeping"
    sleep 1
done

>&2 echo "$url is up - executing command"

/opt/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 &
wait $!
