FROM maven:3.9-eclipse-temurin-19 AS build
WORKDIR /app

COPY . .

RUN mvn -f Zad2_Spring/pom.xml clean package -DskipTests

FROM eclipse-temurin:19-jre
WORKDIR /app

COPY --from=build /app/Zad2_Spring/target/*.jar app.jar

COPY --from=build /app/Zad2_Spring/*.json ./

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]