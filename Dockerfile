FROM adoptopenjdk/openjdk11

ADD esBodyBot-0.0.1-SNAPSHOT.jar /app/bot.jar

ENTRYPOINT ["java","-jar","app/bot.jar"]