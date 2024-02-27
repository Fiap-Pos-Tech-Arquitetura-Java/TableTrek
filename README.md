# TableTrek
Sistema de gerenciamento de mesas, avaliação e comentários de restaurantes,

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
```sh
  mvn test -P system-test
```
- para executar os testes de sistema mas somente os smoke:
```sh
mvn clean test -P system-test -Dcucumber.filter.tags=smoke
```