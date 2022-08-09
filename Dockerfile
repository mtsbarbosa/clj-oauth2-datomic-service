FROM openjdk:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/clj-oauth2-datomic-service-0.0.1-SNAPSHOT-standalone.jar /clj-oauth2-datomic-service/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/clj-oauth2-datomic-service/app.jar"]
