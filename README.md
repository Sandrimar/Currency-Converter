# Currency Converter API

[![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/pt-BR/)
[![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)](https://maven.apache.org)
[![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com)
[![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com)

## Sobre o Projeto

Currency Converter API é um serviço RESTful desenvolvido em Java com Spring Boot e MySQL, capaz de realizar conversões
entre uma vasta gama de moedas, sejam elas reais, criptomoedas ou fictícias. Com operações CRUD completas e atualizações
de taxas de câmbio diariamente, esta API fornece uma solução confiável e eficiente para qualquer necessidade de
conversão monetária.

Este projeto foi
inspirado [nesse desafio](https://github.com/hurbcom/challenge-bravo/blob/main/README.pt.md#-desafio-bravo) e utiliza os
dados [dessa API](https://currencyapi.com) para manter o banco de dados atualizado.

## Moedas Suportadas

Por padrão, as seguintes moedas estão disponíveis para utilização:

- BRL (Real Brasileiro)
- BTC (Bitcoin)
- ETH (Ethereum)
- EUR (Euro)
- USD (Dólar Americano)

No entanto, os usuários têm a opção de ativar outras moedas para uso. Para ver a lista completa de moedas disponíveis e
ativá-las, consulte [este link](https://currencyapi.com/docs/currency-list).

## Como Executar

É necessário ter o [Docker](https://www.docker.com/products/docker-desktop/) e o [Git](https://git-scm.com/downloads)
instalado para executar o projeto.

1. **Clone o repositório:**

```bash
git clone https://github.com/Sandrimar/Currency-Converter.git
```

2. **Navegue até o diretório do projeto:**

```bash
cd Currency-Converter
```

3. **Inicie os serviços usando Docker Compose (pode exigir privilégios elevados, como permissões de superusuário ou
   administrador):**

```bash
docker-compose up --build
```

Quando terminar de usar a API, é importante encerrar os contêineres Docker para liberar os recursos do seu sistema. Para
fazer isso, execute o seguinte comando no diretório do projeto:

```bash
docker-compose down
```

## Endpoints da API

Após iniciar os serviços usando o Docker Compose, a API estará disponível em `http://localhost:8080`.

| Método HTTP | Endpoint              | Descrição                                     | Detalhes                                                 |
|-------------|-----------------------|-----------------------------------------------|----------------------------------------------------------|
| `GET`       | /currencies/available | Lista as moedas disponíveis para uso.         | [Detalhes](#get-currenciesavailable)                     |
| `GET`       | /currencies/{code}    | Retorna uma moeda específica pelo seu código. | [Detalhes](#get-currenciesusd)                           |
| `POST`      | /currencies           | Adiciona uma nova moeda ao sistema.           | [Detalhes](#post-currencies)                             |
| `PUT`       | /currencies/available | Altera a disponibilidade de uma moeda.        | [Detalhes](#put-currenciesavailable)                     |
| `PUT`       | /currencies/{code}    | Atualiza o valor de uma moeda específica.     | [Detalhes](#put-currenciesabc)                           |
| `DELETE`    | /currencies/{code}    | Remove uma moeda do sistema.                  | [Detalhes](#delete-currenciesabc)                        |
| `GET`       | /currencies/convert   | Realiza uma conversão entre duas moedas.      | [Detalhes](#get-currenciesconvertfromusdtobrlamount2536) |

### `GET` /currencies/available

**RESPONSE:** &nbsp; ![Static Badge](https://img.shields.io/badge/200_OK-green)

```json
{
   "BRL": {
      "code": "BRL",
      "value": 5.0569107285,
      "lastUpdate": "2024-04-02T23:59:59Z"
   },
   "BTC": {
      "code": "BTC",
      "value": 0.000015268,
      "lastUpdate": "2024-04-02T23:59:59Z"
   },
   "ETH": {
      "code": "ETH",
      "value": 0.0003049561,
      "lastUpdate": "2024-04-02T23:59:59Z"
   },
   "EUR": {
      "code": "EUR",
      "value": 0.9285001096,
      "lastUpdate": "2024-04-02T23:59:59Z"
   },
   "USD": {
      "code": "USD",
      "value": 1,
      "lastUpdate": "2024-04-02T23:59:59Z"
   }
}
```

### `GET` /currencies/USD

**RESPONSE:** &nbsp; ![Static Badge](https://img.shields.io/badge/200_OK-green)

```json
{
  "code": "USD",
  "value": 1,
  "lastUpdate": "2024-04-02T23:59:59Z"
}
```

### `POST` /currencies

**REQUEST**

```json
{
  "code": "ABC",
  "value": 1.234
}
```

**RESPONSE:** &nbsp; ![Static Badge](https://img.shields.io/badge/201_Created-green)

```json
{
  "code": "ABC",
  "value": 1.234,
  "lastUpdate": "2024-04-03T04:41:44Z"
}
```

### `PUT` /currencies/available

**REQUEST**

```json
{
  "code": "PHP",
  "available": true
}
```

**RESPONSE:** &nbsp; ![Static Badge](https://img.shields.io/badge/200_OK-green)

```json
{
  "code": "PHP",
  "value": 56.2798656768,
  "lastUpdate": "2024-04-02T23:59:59Z"
}
```

### `PUT` /currencies/ABC

**REQUEST**

```json
{
  "value": 0.4321
}
```

**RESPONSE:** &nbsp; ![Static Badge](https://img.shields.io/badge/200_OK-green)

```json
{
  "code": "ABC",
  "value": 0.4321,
  "lastUpdate": "2024-04-03T04:47:34Z"
}
```

### `DELETE` /currencies/ABC

**RESPONSE:** &nbsp; ![Static Badge](https://img.shields.io/badge/204_No_Content-green)

### `GET` /currencies/convert?from=usd&to=brl&amount=25.36

**RESPONSE:** &nbsp; ![Static Badge](https://img.shields.io/badge/200_OK-green)

```json
{
  "fromCurrencyCode": "USD",
  "toCurrencyCode": "BRL",
  "amount": 25.36,
  "result": 128.24325607476,
  "timestamp": "2024-04-03T05:06:35Z"
}
```

## Licença

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://github.com/Sandrimar/Currency-Converter/blob/main/LICENSE)
