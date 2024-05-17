FROM openjdk:8-alpine

COPY target/uberjar/transitions.jar /transitions/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/transitions/app.jar"]
