FROM	nderwin/docker-wildfly:10.1.0.Final.3

LABEL	Author="Nathan Erwin <nathan.d.erwin@gmail.com>"

ARG	finalName

USER	wildfly

# Add the JDBC driver
COPY	target/postgresql-42.1.1.jar /opt/wildfly/standalone/deployments/postgresql-42.1.1.jar
RUN	echo "postgresql-42.1.1.jar.deployed" > /opt/wildfly/standalone/deployments/postgresql-42.1.1.jar.deployed

# Replace the configuration
COPY	target/standalone.xml /opt/wildfly/standalone/configuration/standalone.xml

# Add the file to be deployed
COPY	target/$finalName.war /opt/wildfly/standalone/deployments/$finalName.war

EXPOSE	8080 9990
