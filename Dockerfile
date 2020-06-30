FROM openjdk:13-alpine
USER root
COPY . /app
RUN chmod +x ./mvnw
RUN mkdir /logs
RUN touch /logs/authserver
WORKDIR /app
ENTRYPOINT ["./mvnw"]
CMD ["spring-boot:run"]