FROM gradle:8.12.0-jdk23 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :server:shadowJar --no-daemon

FROM eclipse-temurin:23-jre-alpine

COPY --from=build /home/gradle/src/server/build/libs/ /app/
ENTRYPOINT ["java","-jar","/app/server-all.jar"]