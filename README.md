# micro-playground
A proof of concept repo for Java EE microservices wrapped in Docker containers,
and all run together using Docker Compose.

## Building

To build the .war files and package them into Docker images:

```
mvn -Ddbuser={username} -Ddbpass={password} package
```

## Run Liquibase

To run the Liquibase changesets against a running database:

```
docker-compose up microservdb
mvn -Ddbuser={username} -Ddbpass={password} -Pupdate-database initialize
```

## The Application

To fire up all of the containers:

```
docker-compose up
```

To stop the containers and remove them:

```
docker-compose down
```

## Note

`docker-compose stop` will leave the Wildfly containers in a state where they
won't start again, so it is recommended to use `docker-compose down` for now.
