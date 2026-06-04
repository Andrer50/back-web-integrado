FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk update && apk add --no-cache \
    fontconfig \
    ttf-dejavu

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /workspace/target/*.jar app.jar


ARG SPRING_PROFILE=prod
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILE}
ENV SERVER_PORT=${APP_PORT:-8080}
EXPOSE 8080
USER appuser

ENTRYPOINT ["java","-jar","app.jar"]
