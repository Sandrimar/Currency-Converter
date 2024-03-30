FROM ubuntu:latest

RUN apt-get update && \
    apt-get install -y --no-install-recommends openjdk-21-jdk && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME /usr/lib/jvm/java-21-openjdk-amd64

WORKDIR /app

ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /usr/local/bin/wait-for.sh

RUN chmod +x /usr/local/bin/wait-for.sh

COPY . .

RUN chmod +x mvnw

CMD [ "/usr/local/bin/wait-for.sh", "database:3306", "--", "./mvnw", "clean", "test", "spring-boot:run" ]
