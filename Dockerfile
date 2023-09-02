FROM eclipse-temurin:17-alpine

RUN mkdir /app

WORKDIR /app

COPY target/Triis-1.0-SNAPSHOT.jar /app/triis.jar

EXPOSE 8080

CMD java -jar triis.jar
