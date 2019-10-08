#!/bin/bash

HOME_DIR=~
CALLFLOWS_REPO=$HOME_DIR/cfl/omrs-callflows
CALLFLOWS_OMOD=callflows-1.0.0-SNAPSHOT.omod
CFL_REPO=$HOME_DIR/cfl/cfl-openmrs

cd $CALLFLOWS_REPO
mvn clean install

rm $CFL_REPO/cfl/web/modules/callflows*
mv $CALLFLOWS_REPO/omod/target/$CALLFLOWS_OMOD $CFL_REPO/cfl/web/modules

cd $CFL_REPO/cfl/
docker-compose down
docker-compose up -d

cd $CALLFLOWS_REPO/owa
