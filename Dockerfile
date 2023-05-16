FROM openjdk:17
COPY ./target/retroframe.jar /retroframe/app/retroframe.jar
WORKDIR /retroframe/data
EXPOSE 4242
#HOST PORT 9242
ENTRYPOINT ["java", "-jar","/retroframe/app/retroframe.jar"]