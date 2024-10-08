FROM maven:3.9.9-eclipse-temurin-17 as build

WORKDIR /build

COPY src/main src/main
COPY pom.container.xml pom.xml

RUN mvn clean package -DskipTests

FROM bellsoft/liberica-openjdk-debian:17.0.12-10

WORKDIR /app

COPY --from=build /build/target/rusbonds-crawler-1.0-SNAPSHOT-jar-with-dependencies.jar ./index.jar

ENTRYPOINT ["java", "-jar", "index.jar"]