FROM openjdk:17.0.1-jdk-oracle AS build

ENV POSTGRESDATABASE=table_trek_db
ENV POSTGRESHOST=dpg-cnrmtauct0pc73csu410-a
ENV POSTGRESPASSWORD=lVbYV46Nbxst4P0oChen4mm2weq3T6hg
ENV POSTGRESPORT=5432
ENV POSTGRESUSER=jojo_ramen

RUN echo "POSTGRESDATABASE=${POSTGRESDATABASE}"
RUN echo "POSTGRESHOST=${POSTGRESHOST}"
RUN echo "POSTGRESPASSWORD=${POSTGRESPASSWORD}"
RUN echo "POSTGRESPORT=${POSTGRESPORT}"
RUN echo "POSTGRESUSER=${POSTGRESUSER}"

RUN echo "definindo pasta de trabalho"
WORKDIR /workspace/app

RUN echo "copiando arquivo mvnw necessario para o build"
COPY mvnw .
RUN echo "copiando arquivo .mvn necessario para o build"
COPY .mvn .mvn
RUN echo "copiando arquivo pom.xml necessario para o build"
COPY pom.xml .
RUN echo "copiando diretorio src necessario para o build"
COPY src src

RUN echo "listando o diretorio"
RUN echo $(ls -liah .)

RUN echo "atribuindo permissao maxima para os arquivos"
RUN chmod -R 777 ./mvnw

RUN echo "maven install"
RUN ./mvnw install -DskipTests

RUN echo "listando o diretorio"
RUN echo $(ls -liah .)

RUN echo "criando diretorio de dependencias"
WORKDIR target

RUN echo "listando o diretorio"
RUN echo $(ls -liah .)

RUN echo "inciando docker do Java"
FROM openjdk:17.0.1-jdk-oracle

RUN echo "definindo volume tmp"
VOLUME /tmp

RUN echo "executando a aplicacao usando comando Java"
ENTRYPOINT ["java","-jar","tabletrek-0.0.1-SNAPSHOT.jar"]

RUN echo "sucesso"