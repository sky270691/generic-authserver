FROM openjdk:13-alpine
USER root
COPY . /app
WORKDIR /app
RUN mkdir logs
RUN touch logs/log
RUN chmod +x ./mvnw
ENTRYPOINT ["./mvnw"]
CMD ["spring-boot:run"]