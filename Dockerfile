FROM eclipse-temurin:17-jdk-alpine
ARG JAR_FILE=target/*.jar
ARG DATA_FILE=data/latvia.osm.pbf
COPY ${JAR_FILE} app.jar
COPY ${DATA_FILE} /data/latvia.osm.pbf
ENTRYPOINT ["java", "-jar", "/app.jar"]
