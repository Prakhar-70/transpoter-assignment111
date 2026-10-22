FROM maven:3.10.0-eclipse-temurin-27 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package

FROM eclipse-temurin:27-jre

WORKDIR /app

COPY --from=build /app/target/transpoter-assignment-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000

CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT:-10000}"]
