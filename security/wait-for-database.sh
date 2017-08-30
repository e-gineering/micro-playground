#!/bin/bash
# wait-for-database.sh

set -e

host="$1"
port="$2"
shift 2
cmd="$@"

echo "Checking $host $port before starting Wildfly..."

until nc -z "$host" "$port"; do
    >&2 echo "$host is unavailable - sleeping"
    sleep 1
done

>&2 echo "$host is up - executing command"
exec $cmd
