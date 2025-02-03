# syntax=docker/dockerfile:experimental
FROM azul/zulu-openjdk:8u442-jdk AS build
WORKDIR /home/gradle/src

COPY . /home/gradle/src
RUN chmod +x gradlew
RUN --mount=type=cache,target=/root/.gradle ./gradlew clean build --no-watch-fs --no-daemon --no-build-cache --refresh-dependencies --no-configuration-cache

FROM azul/zulu-openjdk:8u442-8.84-jre-headless

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/workshift-system.jar
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/workshift-system.jar"]
