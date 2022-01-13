if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null ; then
    # RUNNING
    echo "running"
else
    cd /var/www/api.oasisanalitica.com/$2
    nohup ./oasis-api -config="/var/www/delphi/$2.yml" &> /var/www/delphi-logs/$(date +"%m_%d_%Y").log &
    disown
fi