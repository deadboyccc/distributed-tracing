# Stage 1: Extract layers
FROM eclipse-temurin:21-jre-noble AS builder
WORKDIR /extracted
# Ensure only the main executable jar is copied, not the plain jar
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 2: Final Runtime Image
FROM eclipse-temurin:21-jre-noble
WORKDIR /application

# Copy layers from the builder stage
# The order matters: least frequently changed layers first
COPY --from=builder /extracted/dependencies/ ./
COPY --from=builder /extracted/spring-boot-loader/ ./
COPY --from=builder /extracted/snapshot-dependencies/ ./
COPY --from=builder /extracted/application/ ./

EXPOSE 8888

# Use the JarLauncher to start the app using the extracted layers
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
