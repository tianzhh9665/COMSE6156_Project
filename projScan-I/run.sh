#!/bin/sh

sudo chmod 666 /var/run/docker.sock
echo "Project Path Being Scanned: $1"
echo "Code Similarity threshold: $2"

cd $1
echo "Start Scanning Processes. This may take some time to finish..."
docker run --rm -e "WORKSPACE=${PWD}" -v "$PWD:/app" shiftleft/sast-scan scan
copydetect -t $1 -d $2

echo "Generating Analysis Reports..."
javac -cp jsoup-1.14.3.jar:json-simple-1.1.1.jar Application.java
java -cp jsoup-1.14.3.jar:json-simple-1.1.1.jar:. Application $1

echo "Reports have been generated in $1 as Code_Analysis_Report_*, Dependencies_Analysis_Report_*, and Code_Similarity_Analysis_Report_*"

