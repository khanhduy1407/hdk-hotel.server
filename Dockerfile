FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/hdk-hotel-0.0.1-SNAPSHOT.jar hdk-hotel.jar
EXPOSE 9192
ENTRYPOINT ["java", "-jar", "hdk-hotel.jar"]
