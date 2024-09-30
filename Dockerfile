FROM openjdk:21

COPY target/sceni_gateway-0.0.1-SNAPSHOT.jar sceni_gateway-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "sceni_gateway-0.0.1-SNAPSHOT.jar"]

EXPOSE 8080