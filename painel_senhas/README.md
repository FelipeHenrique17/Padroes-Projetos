# 🎟️ Painel de Senhas

Sistema web de gerenciamento e chamada de senhas, desenvolvido como Projeto Integrador do curso de Análise e Desenvolvimento de Sistemas da FATEC Franca.

O objetivo do projeto é organizar o fluxo de atendimento por meio da emissão de senhas normais e prioritárias, gerenciamento de filas e acompanhamento das chamadas.

## 🚀 Funcionalidades

- **Emissão de senhas:** geração automática de senhas normais e prioritárias.
- **Gerenciamento de filas:** organização das senhas conforme prioridade e ordem de chegada.
- **Chamada de senhas:** chamada da próxima senha para atendimento.
- **Controle de guichês:** identificação do guichê responsável pelo atendimento.
- **Finalização de atendimentos:** controle do encerramento de cada atendimento.
- **Painel de exibição:** apresentação da senha atual e das chamadas anteriores.
- **Dashboard:** visualização de estatísticas e indicadores dos atendimentos.
- **Acompanhamento via QR Code:** acesso à tela de acompanhamento por dispositivos móveis.
- **Autenticação:** acesso administrativo protegido por login.

## 🛠️ Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring MVC
- Spring Security
- Thymeleaf
- HTML5
- CSS3
- Maven
- ZXing (geração de QR Code)

## 🏗️ Arquitetura do projeto

O sistema utiliza o padrão arquitetural MVC (Model-View-Controller), com uma camada Service para organizar as regras de negócio.

```text
src/
└── main/
    ├── java/
    │   └── fatecfranca/
    │       └── painel_senhas/
    │           ├── config/
    │           ├── controller/
    │           ├── model/
    │           ├── service/
    │           ├── senha/
    │           └── PainelSenhasApplication.java
    │
    └── resources/
        ├── static/
        ├── templates/
        └── application.properties
```

### Organização das camadas

- **Model:** representa os dados e estados das senhas.
- **View:** contém as interfaces HTML do sistema.
- **Controller:** recebe as requisições e faz a comunicação entre as telas e os serviços.
- **Service:** concentra as regras de negócio e o gerenciamento das filas.
- **Config:** contém as configurações de segurança.
- **Senha:** contém as classes responsáveis pela criação das senhas normais e prioritárias.

O projeto também utiliza o padrão Factory Method para a criação dos diferentes tipos de senha.

## ⚙️ Como executar o projeto

### 1. Pré-requisitos

- Java JDK 21 ou superior.
- IntelliJ IDEA (recomendado).
- Conexão com a internet na primeira execução para baixar as dependências do Maven.

### 2. Clonar o repositório

```bash
git clone URL_DO_REPOSITORIO
```

Entre na pasta do projeto:

```bash
cd Painel-de-Senhas
```

### 3. Configurar o endereço IP

Esta etapa é importante para que outros dispositivos, como celulares, consigam acessar o acompanhamento das senhas através do QR Code.

**Por que configurar o IP?**

O sistema gera um QR Code que direciona para a página de acompanhamento das senhas. Para que o celular consiga abrir essa página, ele precisa utilizar o endereço de rede do computador que está executando a aplicação, em vez de utilizar `localhost`.

**Passo 1 — Descobrir o endereço IPv4**

No computador que executará o sistema:

1. Abra o Prompt de Comando (CMD).
2. Digite o comando:

```bash
ipconfig
```

3. Localize a conexão Wi-Fi ou Ethernet que está sendo utilizada.
4. Procure o campo **Endereço IPv4**.

Exemplo fictício:

```text
Endereço IPv4: 192.168.0.105
```

Utilize o endereço encontrado no seu próprio computador.

**Passo 2 — Configurar o endereço no projeto**

Abra o arquivo:

`src/main/resources/application.properties`

Localize a seguinte configuração:

```properties
painel.url-publica=${PAINEL_URL_PUBLICA:http://seu.ip.aqui:8080}
```

Substitua `seu.ip.aqui` pelo endereço IPv4 encontrado anteriormente.

Exemplo:

```properties
painel.url-publica=${PAINEL_URL_PUBLICA:http://192.168.0.105:8080}
```

Mantenha a porta `8080`.

**Importante:** não utilize `localhost` nessa configuração quando quiser acessar o QR Code pelo celular. O endereço `localhost` representa o próprio dispositivo que está tentando acessar o sistema.

### 4. Configurar o acesso administrativo

No mesmo arquivo `application.properties`, localize:

```properties
spring.security.user.name=${PAINEL_ADMIN_USUARIO:admin}
spring.security.user.password=${PAINEL_ADMIN_SENHA:2232}
```

As credenciais padrão para demonstração são:

- Usuário: `admin`
- Senha: `2232`

É possível alterar os valores padrão ou utilizar as variáveis de ambiente `PAINEL_ADMIN_USUARIO` e `PAINEL_ADMIN_SENHA`.

Não utilize as credenciais de demonstração em ambientes de produção.

### 5. Executar a aplicação

No terminal do IntelliJ, dentro da pasta que contém o `pom.xml`, execute:

```bash
.\mvnw.cmd spring-boot:run
```

Também é possível executar diretamente a classe:

`PainelSenhasApplication.java`

Aguarde o Spring Boot iniciar completamente.

### 6. Acessar o sistema

No computador, abra o navegador e acesse:

| Funcionalidade | Endereço |
|---|---|
| Painel principal | http://localhost:8080/ |
| Login | http://localhost:8080/login |
| Dashboard | http://localhost:8080/dashboard |
| Display de senhas | http://localhost:8080/display |
| Acompanhamento | http://localhost:8080/acompanhar |

### 7. Acessar pelo celular utilizando QR Code

Para utilizar o acompanhamento pelo celular:

1. Inicie o sistema no computador.
2. Certifique-se de que o computador e o celular estejam conectados à mesma rede Wi-Fi.
3. Acesse o Display de Senhas pelo computador.
4. Localize o QR Code exibido na tela.
5. Utilize a câmera do celular para escanear o código.
6. O navegador abrirá a página de acompanhamento das senhas.

**Atenção:**

- O celular e o computador precisam estar na mesma rede local.
- O endereço IPv4 configurado precisa corresponder ao computador que está executando o sistema.
- A porta `8080` precisa estar acessível na rede.
- Caso o Firewall do Windows solicite autorização para o Java, permita o acesso em redes privadas.
- Se o endereço IPv4 mudar, execute novamente `ipconfig` e atualize a configuração.

## 📖 Como utilizar

1. Acesse o sistema e realize o login administrativo.
2. Emita uma senha normal ou prioritária.
3. A senha será adicionada à fila de espera.
4. Utilize a opção de chamar próxima senha.
5. O painel exibirá a senha chamada e o guichê correspondente.
6. Finalize o atendimento quando concluído.
7. Consulte o dashboard para acompanhar os indicadores.
8. Utilize o QR Code para permitir o acompanhamento pelo celular.

## 👥 Integrantes 

1. Anael Monte Paz de Araújo
2. Felipe Henrique de Oliveira
3. Gabriel Aparecido Messias
4. Moisés Kennedy Batista Mendes
5. Vinícius Carrijo de Souza
6. Vinícius Meloni

## 🎓 Projeto Integrador

Projeto desenvolvido como parte do curso de Análise e Desenvolvimento de Sistemas da FATEC Franca, aplicando conceitos de orientação a objetos, desenvolvimento web com Java, arquitetura MVC e padrões de projeto.

---

Desenvolvido para fins acadêmicos e de aprendizado.
