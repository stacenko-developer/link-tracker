FROM openjdk:23
WORKDIR /app

COPY target/bot-1.0.jar /app/bot-1.0.jar
EXPOSE 8080
EXPOSE 8000

CMD ["java", "-jar", "bot-1.0.jar"]
