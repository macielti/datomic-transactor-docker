FROM clojure:temurin-21-tools-deps-bookworm-slim

RUN apt-get update && apt-get install -y curl unzip iputils-ping && rm -rf /var/lib/apt/lists/*

RUN curl -s https://raw.githubusercontent.com/babashka/babashka/master/install | bash

RUN curl \
      https://datomic-pro-downloads.s3.amazonaws.com/1.0.7187/datomic-pro-1.0.7187.zip \
      -o datomic-pro.zip \
      && unzip datomic-pro.zip \
      && mv datomic-pro-1.0.7187 /usr/datomic-pro

WORKDIR /usr/datomic-pro

RUN rm /usr/datomic-pro/lib/postgresql-42.5.1.jar

COPY postgresql-42.7.4.jar /usr/datomic-pro/lib/postgresql-42.7.4.jar
COPY generate-properties.clj /usr/config/generate-properties.clj

CMD hostname -i | bb /usr/config/generate-properties.clj && \
    bin/transactor config/transactor.properties