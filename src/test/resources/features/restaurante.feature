#language: pt

  Funcionalidade: Restaurante
    @smoke @high
    Cenario: Registrar Restaurante
      Quando registrar um novo restaurante
      Entao o restaurante é registrado com sucesso
      E deve ser apresentado
    @smoke @low
    Cenario: Buscar Restaurante
      Dado que um restaurante já foi registrado
      Quando efetuar a busca do restaurante
      Entao o restaurante é exibido com sucesso
    @smoke
    Cenario: Alterar Restaurante
      Dado que um restaurante já foi registrado
      Quando efetuar uma requisição para alterar restaurante
      Entao o restaurante é atualizado com sucesso
      E deve ser apresentado
    @high
    Cenario: Remover Restaurante
      Dado que um restaurante já foi registrado
      Quando requisitar a remoção do restaurante
      Entao o restaurante é removido com sucesso