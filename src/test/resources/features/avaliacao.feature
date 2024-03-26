#language: pt

  Funcionalidade: Avaliacao
    @smoke @high
    Cenario: registrar avaliacao
      Dado que tenho um usuario registrado para fazer uma avaliacao
      Dado que tenho um usuario registrado para cadastrar um restaurante
      Dado que tenho um restaurante registrado para fazer uma avaliacao
      Dado que tenho uma reserva de mesa registrada para fazer uma avaliacao
      Quando registrar uma nova avaliacao
      Entao a avaliacao é registrada com sucesso
      E a avaliacao deve ser apresentada
    @smoke @low
    Cenario: buscar avaliacao
      Dado que tenho um usuario registrado para fazer uma avaliacao
      Dado que tenho um usuario registrado para cadastrar um restaurante
      Dado que tenho um restaurante registrado para fazer uma avaliacao
      Dado que tenho uma reserva de mesa registrada para fazer uma avaliacao
      Dado que uma avaliacao já foi registrada
      Quando efetuar a busca de uma avaliacao
      Entao a avaliacao é exibida com sucesso
    @smoke
    Cenario: alterar avaliacao
      Dado que tenho um usuario registrado para fazer uma avaliacao
      Dado que tenho um usuario registrado para cadastrar um restaurante
      Dado que tenho um restaurante registrado para fazer uma avaliacao
      Dado que tenho uma reserva de mesa registrada para fazer uma avaliacao
      Dado que uma avaliacao já foi registrada
      Quando efetuar uma requisição para alterar uma avaliacao
      Entao a avaliacao é alterada com sucesso
      E a avaliacao deve ser apresentada
    @high
    Cenario: remover avaliacao
      Dado que tenho um usuario registrado para fazer uma avaliacao
      Dado que tenho um usuario registrado para cadastrar um restaurante
      Dado que tenho um restaurante registrado para fazer uma avaliacao
      Dado que tenho uma reserva de mesa registrada para fazer uma avaliacao
      Dado que uma avaliacao já foi registrada
      Quando requisitar a remoção de uma avaliacao
      Entao a avaliacao é removida com sucesso