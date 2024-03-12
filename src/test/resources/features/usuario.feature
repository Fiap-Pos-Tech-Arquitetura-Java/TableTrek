#language: pt

  Funcionalidade: Usuario
    @smoke @high
    Cenario: Registrar Usuario
      Quando registrar um novo usuario
      Entao o usuario é registrado com sucesso
      E usuario deve ser apresentado
    @smoke @low
    Cenario: Buscar Usuario
      Dado que um usuario já foi registrado
      Quando efetuar a busca do usuario
      Entao o usuario é exibido com sucesso
    @smoke
    Cenario: Alterar Usuario
      Dado que um usuario já foi registrado
      Quando efetuar uma requisição para alterar usuario
      Entao o usuario é atualizado com sucesso
      E usuario deve ser apresentado
    @high
    Cenario: Remover Usuario
      Dado que um usuario já foi registrado
      Quando requisitar a remoção do usuario
      Entao o usuario é removido com sucesso