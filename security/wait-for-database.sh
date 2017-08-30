#!/bin/bash
# wait-for-database.sh

set -e

host="microservdb"
port="5432"

function _shutdown() {
    echo "Received TERM signal, shutting down now..."
    /opt/wildfly/bin/jboss-cli.sh -c ":shutdown(timeout=3)"
    wait $!
    exit 0
}

trap _shutdown SIGTERM

echo "Checking $host $port before starting Wildfly..."

until nc -z "$host" "$port"; do
    >&2 echo "$host is unavailable - sleeping"
    sleep 1
done

>&2 echo "$host is up - executing command"

/opt/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 &
wait $!
