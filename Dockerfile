FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY ./gradlew .

COPY ./gradlew.bat .

COPY ./gradle ./gradle

COPY ./build.gradle.kts .

COPY ./settings.gradle.kts .

COPY ./src ./src

RUN apk add --no-cache bash

RUN chmod +x gradlew

RUN ./gradlew build -x test

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
