#!/bin/bash
# wait-for-security.sh

set -e

url="$1"
shift
cmd="$@"

echo "Checking $url before starting Wildfly..."

until curl -fs "$url"; do
    >&2 echo "$url is unavailable - sleeping"
    sleep 1
done

>&2 echo "$url is up - executing command"
exec $cmd

