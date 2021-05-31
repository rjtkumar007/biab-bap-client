FROM openjdk:11-jre-slim
# try out alpine/debian

ENV SERVER_PORT $SERVER_PORT

COPY build/libs/sandbox_bap-*.*.*-SNAPSHOT.jar /usr/local/lib/sandbox_bap.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /usr/local/lib/sandbox_bap.jar"]
