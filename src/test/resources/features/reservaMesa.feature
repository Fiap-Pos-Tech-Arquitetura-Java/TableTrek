#language: pt

  Funcionalidade: Reserva de Mesa
    @smoke @high
    Cenario: registrar reserva de mesa
      Dado que tenho um usuario registrado para reservar uma mesa
      Dado que tenho um restaurante registrado para reservar uma mesa
      Quando registrar uma nova reserva de mesa
      Entao a reserva de mesa é registrada com sucesso
      E a reserva de mesa deve ser apresentada
    @smoke @low
    Cenario: buscar reserva de mesa
      Dado que tenho um usuario registrado para reservar uma mesa
      Dado que tenho um restaurante registrado para reservar uma mesa
      Dado que uma reserva de mesa já foi registrada
      Quando efetuar a busca de uma reserva de mesa
      Entao a reserva de mesa é exibida com sucesso
    @smoke
    Cenario: finalizar reserva de mesa
      Dado que tenho um usuario registrado para reservar uma mesa
      Dado que tenho um restaurante registrado para reservar uma mesa
      Dado que uma reserva de mesa já foi registrada
      Quando efetuar uma requisição para finalizar uma reserva de mesa
      Entao a reserva de uma mesa é finalizada com sucesso
      E a reserva de mesa deve ser apresentada
    @high
    Cenario: remover reserva de mesa
      Dado que tenho um usuario registrado para reservar uma mesa
      Dado que tenho um restaurante registrado para reservar uma mesa
      Dado que uma reserva de mesa já foi registrada
      Quando requisitar a remoção de uma reserva de mesa
      Entao a reserva de mesa é removida com sucesso