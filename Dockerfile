# build stage
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon

COPY src ./src

# build
RUN gradle build -x test --no-daemon

# runtime stage
FROM amazoncorretto:21
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
