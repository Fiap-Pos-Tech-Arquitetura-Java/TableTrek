# TableTrek
Sistema de gerenciamento de mesas, avaliação e comentários de restaurantes,

# Instalação

# Testes
- para executar os testes unitários:
```sh 
  mvn test
  ```
- para executar os testes integrados:
```sh
  mvn test -P integration-test
```
- para executar os testes de sistema:
  - é preciso retirar o comentário da propriedade cucumber.features no arquivo src/test/resources/junit-plataform.properties
```sh
  mvn test -P system-test
```
- para executar os testes de sistema mas somente os smoke:
```sh
mvn clean test -P system-test -Dcucumber.filter.tags=smoke
```

# Postman
há uma posta postman na raiz do projeto contendo:
- a coleção de requisições para fazer uma avaliação
- um enviroment para execução local
- um enviroment para execução no AWS
- um enviroment para execução no render