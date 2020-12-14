#!/bin/bash
git pull
mvn clean
mvn surefire-report:report -Dooad.group=$0 -Dooad.testdir=$1 -Dmanagement.gate=$2 -Dmall.gate=$3
mvn site:deploy
