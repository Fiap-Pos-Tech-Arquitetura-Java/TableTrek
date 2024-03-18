FROM openjdk:17 AS build

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

RUN echo "listando o diretorio"
RUN echo $(pwd)

RUN echo "listando o diretorio"
RUN echo $(ls -liah .)

RUN echo "atribuindo permissao maxima para os arquivos"
RUN chmod -R 777 ./mvnw

RUN echo "maven install"
RUN ./mvnw install -DskipTests

RUN echo "criando diretorio de dependencias"
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

RUN echo "inciando docker do Java"
FROM openjdk:17

RUN echo "definindo volume tmp"
VOLUME /tmp

RUN echo "definindo parametro ARGS"
ARG DEPENDENCY=/workspace/app/target/dependency

RUN echo "copiando artefatos para o diretorio de execucao"
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

RUN echo "executando a aplicacao usando comando Java"
ENTRYPOINT ["java","-cp","app:app/lib/*","br.com.fiap.postech.tabletrek.TabletrekApplication"]

RUN echo "sucesso"