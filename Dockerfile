FROM maven:3.8-openjdk-11-slim as build
COPY . /src
WORKDIR /src
RUN mvn clean package && cd target && mv *.jar app.jar
EXPOSE 3000

FROM openjdk:11-slim
COPY --from=build /src/target/app.jar /usr/app.jar
ENTRYPOINT ["java","-jar","/usr/app.jar"]
EXPOSE 3000
