FROM	nderwin/docker-wildfly:10.1.0.Final.3

LABEL	Author="Nathan Erwin <nathan.d.erwin@gmail.com>"

ARG	finalName

USER	wildfly

# Add the file to be deployed
COPY	target/$finalName.war /opt/wildfly/standalone/deployments/$finalName.war

EXPOSE	8080 9990
