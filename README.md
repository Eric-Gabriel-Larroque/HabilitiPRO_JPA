# Projeto HabilitiPRO JPA com Hibernate

> Projeto do Módulo 2 do programa de capacitação TECH-DIVE, primeiro projeto em maven, com persistência de dados
> e conexão ao banco de dados com PostgreSQL


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

## Classes e suas funcionalidades:

### **1. Application** 

  Contém somente a classe **Program** para execução das funcionalidades do projeto;
  
### **2. Connection**

  É onde está disposta a classe **JpaConnectionFactory** para criação de um gerenciador de entidades e a classe **Transaction** para abertura e fechamento de transações;
  
### **3. Enums**

  Possui as seguintes classes:
  
- **Perfis -** Atributo da classe **Usuário**, one contém os possíveis tipos de perfis de acesso;
- **Regional -** Atributo da classe **Empresa**, delimitando a escolha da regional dentre as dispostas;
- **Segmento -** Também outro atributo da classe **Empresa**; e
- **Status -** Atributo da classe **Modulo**, sendo o gerenciamento do status escolhido da classe **Usuário**

### **4. Model/Persistence**

  - **Trabalhador** - Entidade com vinculo a uma determinada **Empresa**, **Trilha** e **Módulo**, sendo a **Trilha** e **Módulo** escolhidos de acordo com a Empresa com a qual o **Trabalhador** possui um vínculo. 
  - **Empresa** - Como o próprio nome sugere, é abstração de uma empresa com seus diversos atributos, como nome e CNPJ. Responsável por delegar e delimitar a(s) trilha(s) que o **Trabalhador**, vinculado àquela **Empresa**, deverá escolher.
  - **Trilha** - Classe onde se define para qual **Ocupação** e **Empresa** a mesma será destinada, possui um nome getado automaticamente de acordo com sua ocupação e empresa;
  - **Modulo** - Possui a trilha para qual determinado módulo será destinado, com uma data inicial e final para ser executado, possuindo tmbém um prazo limite para ser de fato finalizado e a trilha poder ser avaliada pelo Usuário com determinado perfil de acesso;
  - **Funcao** - Entidade atribuída diretamente ao **Trabalhador**, definindo nada mais além do que o nome para a função que ele exerce;
  - **Ocupacao** - Entidade com mesmo intuito de **Funcao**, define por meio de caracteres textuais o nome dado a ocupação para qual determinada **Trilha** será destinada;
  - **HistoricoTrabalhador** - Entidade responsável pelo registro de criação,mudança de setor, mudança de função e mudança de empresa realizada pelo **Trabalhador**;
  - **Anotacao** - Entidade responsável por armazenar a anotação realizada pelo trabalhador referente a um determinado módulo;
  - **Score** - Juntamente com a anotação realizada pelo Trabalhador, o mesmo pode também definir um score para cada módulo que ele participou;
  - **Setor** - Como a classe **Funcao**, é um atributo da entidade **Trabalhador** utilizada para definir a qual setor o **Trabalhador pertence**;
  - **Usuario** - Responsável por gerenciar o status de cada módulo, realizar anotações e inserir uma notas referentes às trilhas, tudo de acordo com o(s) perfi(l/s) de acesso que obtiver;

### **5 - Model/DAO**

  Pasta onde é realizada o CRUD de cada uma das entidades. Quase todas possuem as mesmas operações, como 
  
  - **Create -** Cria uma novo registro de determinada entidade;
  - **Delete -** Deleta um regstro de determinada entidade;
  - **Update -** Atualiza os dados de determinada entidade;
  - **listAll -** Lista todos os registros;
  - **listByName -** Lista pelo atributo _nome_ todos os registros que contenham o nome informado;
  - **findByName -** Retorna apenas um registro que contenha o nome informado;
  - **getById -** Retorna apenas um registro de determinada entidade pelo seu Id; 

  Em classes passíveis de ter um nome igual ao de outro registro, seu _findByName_ é inutilizado ou readaptado, como é o caso da entidade **Empresa** com o _findByCnpj_ e **Trabalhador** com o _findByCpf_ 
  
### **6 - Service**

  Faz a validação de cada uma das transações de cada uma das entidades supracitadas recebendo os métodos importados da classe **Validation**;
  
### **7 - Util**

  Pacote de utilitários onde está armazenada as classes:
  
  - **Validation -** Que contém os métodos para validação de desde objetos até atributuos das entidas.
  - **Input -** Contendo apenas um método de validação de caracteres textuais inseridos pelo usuário.
