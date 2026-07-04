FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /workspace

RUN mkdir -p /workspace/uploads/images

COPY uploads ./uploads
COPY --from=build /build/target/PCOnlineShop-0.0.1-SNAPSHOT.jar /workspace/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/workspace/app.jar"]
