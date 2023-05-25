FROM adoptopenjdk/openjdk11

ADD logicaScoolBot-0.0.1-SNAPSHOT.jar /app/bot.jar

ENTRYPOINT ["java","-jar","app/bot.jar"]
