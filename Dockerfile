FROM openjdk:21

ADD target/sceni_gateway-0.0.1-SNAPSHOT.jar sceni_gateway-0.0.1-SNAPSHOT

ENTRYPOINT [ "java","-jar","sceni_gateway-0.0.1-SNAPSHOT" ]

EXPOSE 8080