#!/bin/bash
# run.sh

set -e

host="microservdb"
port="5432"

function _shutdown() {
    echo "Received TERM signal, shutting down now..."
    /opt/wildfly/bin/jboss-cli.sh -c ":shutdown(timeout=3)"
    exit 0
}

trap _shutdown SIGTERM

echo "Checking $host $port before starting Wildfly..."

until nc -z "$host" "$port"; do
    >&2 echo "$host is unavailable - sleeping"
    sleep 1
done

>&2 echo "$host is up - executing command"

# Start Wildfly
/opt/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 &

# Remember the process Id we just forked
wf_pid=$!

until nc -z localhost 9990; do
	>&2 echo "...waiting for Wildfly to start..."
	sleep 1
done

>&2 echo "Deploying JDBC driver..."
# Deploy the JDBC driver
/opt/wildfly/bin/jboss-cli.sh --connect --command="deploy --force /opt/postgresql-42.1.1.jar"

>&2 echo "Defining datasource..."
# Define the datasource
/opt/wildfly/bin/jboss-cli.sh --connect --command="data-source add \
	--name=shareDS \
	--connection-url=jdbc:postgresql://microservdb:5432/microserv \
	--driver-name=postgresql-42.1.1.jar \
	--jndi-name=java:/shareDS \
	--driver-class=org.postgresql.Driver \
	--jta=true \
	--user-name=`cat /run/secrets/postgres_username` \
	--password=`cat /run/secrets/postgres_password` \
	--enabled=true \
	--use-ccm=true \
	--valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker \
	--background-validation=true \
	--exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter"

>&2 echo "Deploying the app..."
# Deploy our app
/opt/wildfly/bin/jboss-cli.sh --connect --command="deploy --force `ls /opt/*.war`"

# Wait here for Wildfly to stop running
wait $wf_pid
