# build(Gradle + Corretto 21)
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# execute(Amazon Corretto 21 JRE)
FROM amazoncorretto:21
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
