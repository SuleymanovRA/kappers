FROM woahbase/alpine-openjdk8:latest

WORKDIR /app
COPY target/kappers.war kappers.war

ENTRYPOINT exec java $JAVA_OPTS -jar kappers.war