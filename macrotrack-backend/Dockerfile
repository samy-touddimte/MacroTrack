# Build stage
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs

EXPOSE 8080

# On limite la RAM à 256 Mo pour laisser de la marge au système
ENV JAVA_OPTS="-Xmx256m -Xms256m"

# Lance l'app avec ces paramètres
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]