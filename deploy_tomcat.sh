#!/bin/sh
rm -rf /var/lib/tomcat7/webapps/ROOT
cp semdroid-server/build/libs/semdroid-server.war /var/lib/tomcat7/webapps/ROOT.war
service tomcat7 restart
