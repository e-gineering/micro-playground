# Start from this image
FROM	nderwin/docker-wildfly:10.1.0.Final.3

# Who to credit/blame for this image
LABEL	Author="Nathan Erwin <nathan.d.erwin@gmail.com>"

# Will contain the name of the war file for the project
ARG		finalName

# Install a tool that will help determine if other containers are running
USER	root
RUN		apt-get update && apt-get -y install netcat-openbsd

# Make sure all commands are run as the wildfly user instead of root
USER	wildfly

# Add in the command script
COPY	run.sh /opt/wildfly

# Add the JDBC driver
COPY	target/postgresql-42.1.1.jar /opt/postgresql-42.1.1.jar

# Add the file to be deployed
COPY	target/$finalName.war /opt/$finalName.war

EXPOSE	8080 9990

HEALTHCHECK CMD curl -f http://security:8080/security/resources/health

CMD	["/opt/wildfly/run.sh"]
