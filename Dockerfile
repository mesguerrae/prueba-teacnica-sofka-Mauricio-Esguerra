FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
# Quitamos -q para ver el error real de Maven en los logs de docker build
RUN mvn -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

ENV JAVA_OPTS="-Xms256m -Xmx512m"

COPY --from=build /app/target/pagos-interbancarios-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

