# Start from this image
FROM	nderwin/docker-wildfly:latest

# Who to credit/blame for this image
LABEL	Author="Nathan Erwin <nathan.d.erwin@gmail.com>"

# Will contain the name of the war file for the project
ARG	finalName

# Make sure all commands are run as the wildfly user instead of root
USER	wildfly

# Add in the command script
COPY	run.sh /opt/wildfly

# Add the file to be deployed
COPY	target/$finalName.war /opt/wildfly/standalone/deployments/$finalName.war

EXPOSE	8080 9990

HEALTHCHECK CMD curl -f http://serviceone:8080/serviceone/resources/health

CMD	["/opt/wildfly/run.sh"]
