FROM eclipse-temurin:19-jdk-alpine
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn /app/.mvn
RUN ./mvnw dependency:go-offline
COPY src ./src
CMD ["./mvnw", "spring-boot:run"]