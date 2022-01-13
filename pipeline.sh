#!/bin/bash

echo "Args"
echo $0 $1 $2 $3
kill -9 $(lsof -t -i:$1) &> /dev/null ||  echo "Was not running on port $1"
mkdir -p "/var/www/delphi-logs/$2"
echo "Logs on -> /var/www/delphi-logs/$2"
echo "Booting app"

nohup java -jar -Dspring.profiles.active=$2 $3/$2/delphi-0.0.1-SNAPSHOT.jar &> /var/www/delphi-logs/$2/$(date +"%m_%d_%Y").log &
echo "Logs available on /var/www/delphi-logs/$2/$(date +"%m_%d_%Y").log"
disown
echo "App booted"