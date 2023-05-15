FROM openjdk:17

WORKDIR /usr/src/app

COPY ./build/libs/demo-0.0.1-SNAPSHOT.jar ./build/libs/demo-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java","-jar","./build/libs/demo-0.0.1-SNAPSHOT.jar"]