FROM openjdk:13-alpine
USER root
COPY . /app
WORKDIR /app
RUN chmod +x ./mvnw
RUN mkdir /logs
RUN touch /logs/authserver
ENTRYPOINT ["./mvnw"]
CMD ["spring-boot:run"]