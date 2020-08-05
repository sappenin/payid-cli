FROM openjdk:11-jre-slim
RUN addgroup --system payid
RUN adduser --ingroup payid --system payid
USER payid
VOLUME /tmp
COPY target/payid-cli-HEAD-SNAPSHOT.jar /payid-cli.jar
EXPOSE 5432
ENTRYPOINT [ "sh", "-c", "java -noverify -XX:TieredStopAtLevel=1 ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /payid-cli.jar --spring.config.location=classpath:/application.properties --spring.jmx.enabled=false" ]
