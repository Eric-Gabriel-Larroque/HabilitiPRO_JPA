# Projeto HabilitiPRO JPA com Hibernate

> Projeto do Módulo 2 do programa de capacitação TECH-DIVE, primeiro projeto em maven, com persistência de dados
> e conexão ao banco de dados

## Estruturação do Projeto:

![](img/img.png)

O projeto é composto pelas seguintes partições:

- **Application** - Local onde ocorre a execução das funcionalidades do projeto;
- **Connection** - Que cria e gerencia uma conexão ao banco de dados;
- **Enums** - Contém classes, do tipo Enum, para delimitar a escolha dentre os valores disponíveis;
- **Model** - Partição contendo as Entidades e sua camada de acesso à dados, dividida em duas outras:
  - **DAO** - Contém o CRUD de cada uma das entidades; e
  - **Persistence** - Armazena a abstração das Entidades para uma classe própria;
- **Service** - Camada de serviço, onde se valida cada uma das transações realizadas;
- **Util** - Contendo, em suma, classes para validação de dados.
