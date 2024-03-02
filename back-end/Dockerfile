FROM gradle:8.3-jdk17 as build
COPY --from=soundseeker-gradle_build /home/gradle/src/build/libs/api-0.0.1-SNAPSHOT.jar /app.jar

FROM amazoncorretto:17.0.10-alpine
VOLUME /tmp
COPY --from=build /app.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]