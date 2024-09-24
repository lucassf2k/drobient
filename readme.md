# Projeto de Simulação de Comunicação Indireta com Drones e Centro de Dados

Este projeto simula a comunicação indireta entre drones e um centro de dados, onde drones sobrevoam ambientes coletando dados climáticos (pressão atmosférica, radiação solar, temperatura e umidade) e os enviam para servidores que replicam os dados em uma base de dados. Usuários podem acessar os dados por meio de multicast ou conexão direta.

## Cenário Geral
- **Drones** sobrevoam um ambiente e, através de sensores, coletam dados climáticos.
- **Centro de Dados**: os dados coletados são enviados para servidores do centro de dados, que realizam operações de escrita na base de dados replicada.
- **Usuários** podem acessar os dados coletados, tanto de dentro quanto de fora do centro de dados, utilizando diferentes métodos de comunicação.

---

## Simulações

### A. Simulação: Grupo Aberto

Nesta simulação, o sistema realiza comunicação em grupo aberto usando multicast.

#### Cenário
1. **Drone**:
    - Coleta dados climáticos a cada **3 segundos**.
    - Envia os dados para um **servidor do centro de dados**.

2. **Servidor**:
    - Realiza a **escrita** dos dados em sua base de dados (na memória principal, por exemplo).
    - Envia os dados para um **IP multicast**.

3. **Usuários**:
    - Três processos de usuários podem **se juntar ao grupo multicast** para consumir os dados.

#### Funcionamento
1. **Coleta de Dados**:
    - O drone inicia a coleta ao receber um comando no console.
    - Dados aleatórios, dentro de faixas aceitáveis, são gerados para cada elemento climático.

2. **Simulação**:
    - Tempo de execução: **3 minutos**.
    - Ao final, será gerado um **log** com todos os dados coletados e as requisições dos usuários.

---

### B. Simulação: Grupo Fechado

Nesta simulação, um grupo fechado de servidores é utilizado para processar e distribuir os dados coletados por dois drones que monitoram duas regiões diferentes.

#### Cenário
1. **Drones**:
    - Dois drones monitoram as regiões **Norte** e **Sul**, com faixas de valores climáticos específicas para essas regiões.
    - Cada drone tem conexão com um servidor no centro de dados:
        - **Drone 1**: envia dados para o **Servidor 1**.
        - **Drone 2**: envia dados para o **Servidor 2**.

2. **Servidores**:
    - **Servidores 1 e 2**: Recebem os dados dos drones e os armazenam.
    - **Grupo Fechado**: Um grupo de servidores (Servidores 1, 2, 3 e 4) forma um grupo fechado, onde os servidores 1 e 2 transmitem dados via multicast para o grupo.

3. **Usuários**:
    - Usuários podem acessar os dados a partir dos **Servidores 3 e 4** via multicast ou conexão direta.

#### Funcionamento
1. **Coleta e Distribuição de Dados**:
    - Servidores 1 e 2 recebem os dados dos drones e os transmitem para o grupo fechado de servidores (1, 2, 3, 4).
    - Usuários se conectam aos Servidores 3 e 4 para acessar os dados.

2. **Simulação**:
    - Tempo de execução: **3 minutos**.
    - Um log final é gerado com todos os dados coletados e distribuídos.

---

## Requisitos

- **Java 21**.
- **IDE**: IntelliJ IDEA.
- **Multicast Networking**: Os sistemas de usuários e servidores devem se conectar ao IP multicast para ler os dados.
- **Simulação de sensores**: O sistema simula a coleta de dados climáticos com valores aleatórios dentro de uma faixa predefinida.

---

## Como Rodar o Projeto

1. **Clone o Repositório**:
   ```bash
   git clone git@github.com:lucassf2k/drobient.git