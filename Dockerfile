FROM eclipse-temurin:25-jre-alpine

WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=90.0", "-jar", "app.jar"]