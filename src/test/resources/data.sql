insert into tb_restaurante
    (id, nome, localizacao, horarioFuncionamento, capacidade, tipocozinha)
values
    ('52a85f11-9f0f-4dc6-b92f-abc3881328a8', 'JoJo Ramen', 'R. Dr. Rafael de Barros, 262 - Paraíso, São Paulo - SP, 04003-041', '11:30–14:30 18:00–22:00', 10, 'Japonesa'),
    ('b35d3a29-408a-4d1a-964c-2261cb0e252f', 'Tojiro Sushi', 'Rua Bernardino Fanganiello, 410 - Casa Verde, São Paulo - SP, 02512-000', '11:30–15:00 18:30–23:00', 40, 'Japonesa'),
    ('ada8399b-44f0-499c-82d9-5ca9ed1670da', 'Casa da costela', 'Av. Min. Petrônio Portela, 1009 - Moinho Velho, São Paulo - SP, 02959-000', '12:00–22:00', 50, 'Brasileira');

insert into tb_usuario
(id, nome, email, senha, telefone)
values
    ('d32c6406-a4a2-4503-ac12-d14b8a3b788f', 'Anderson Wagner', 'anderson.wagner@gmail.com', '$2a$10$7PsRzYVJ4MtyKu/6hYj/H.3pwbFc2DFHvTj8ml0MPQqmNbBBEQ8ha', 11991733344),
    ('a6df9ca4-09d7-41a1-bb5b-c8cb800f7452', 'Kaiby', 'aaaa@bbb.com', '$2a$10$ZNnvQiUPfufFLO6knvEcCeueCOrLCawcflggb8Ab94T2viYM/15zG', 11999999999),
    ('ffd28058-4c16-41ce-9f03-80dfbc177aaf', 'Janaína', 'ccc@ddd.com', '$2a$10$HH.CPxp0olhFkl.5jhok7OO5HsDjFnW.xPH2jz0dtFmCaF.ezs1v2', 11988886731);