# 🥦 Smart Food Basket — ODS Edition

> Trabalho Prático da cadeira de **Programação Orientada por Objetos**
> Licenciatura em Engenharia Informática — Instituto Politécnico de Beja
> Ano Letivo 2025/2026

Uma aplicação JavaFX inspirada em jogos de *tiles* que promove escolhas alimentares saudáveis e sustentáveis, alinhada com os Objetivos de Desenvolvimento Sustentável da ONU (ODS 2, 3 e 12).

---

## 📋 Índice

- [Sobre o projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Como executar](#como-executar)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Testes](#testes)
- [ODS abrangidos](#ods-abrangidos)
- [Autores](#autores)

---

## 🎯 Sobre o projeto

O **Smart Food Basket** é um *Cabaz Alimentar Inteligente* que atua como um "treinador de sustentabilidade". O utilizador constrói um cabaz de compras a partir de um supermercado virtual e a aplicação calcula em tempo real o custo, as calorias e verifica se o cabaz cumpre metas saudáveis e sustentáveis.

Cada alimento é representado por um *tile* colorido de acordo com o seu grupo da Roda dos Alimentos, oferecendo uma experiência visual e interativa semelhante a um jogo.

---

## ✨ Funcionalidades

### Requisitos essenciais implementados

- 🏗️ **Hierarquia de classes** com herança e polimorfismo (FoodItem abstrato → PackagedFood / BulkFood)
- 🧪 **Testes unitários JUnit** para validar os cálculos de preços e metas
- 📄 **Leitura de CSV** — carrega alimentos a partir de um ficheiro com tratamento de erros
- 🎨 **Interface gráfica JavaFX** com grelha de *tiles* coloridos por grupo alimentar
- 🖼️ **Imagens reais** para cada alimento
- 💶 **Dashboard em tempo real** com custo, calorias e lista de itens
- 🛒 **Dois tipos de produtos** — embalados (unidade fixa) e a granel (peso variável)
- 📝 **Exportação de recibo** em ficheiro `.txt` com data automática
- 🌱 **Validação da meta ODS 3** (mínimo 400g de vegetais no cabaz)

### Diferenciação visual de produtos

| Tipo | Identificação | Exemplo |
|---|---|---|
| 📦 Embalado | `€/un.` | Arroz, Leite, Queijo |
| ⚖️ Granel | `€/kg` | Brócolos, Maçã, Frango |

Ao clicar num produto a granel, a aplicação abre um diálogo a pedir o peso desejado em gramas e calcula automaticamente o preço proporcional.

---

## 🛠️ Tecnologias utilizadas

- **Java 17+** — linguagem base
- **JavaFX** — interface gráfica
- **JUnit 5** — testes unitários
- **Maven** — gestão de dependências
- **IntelliJ IDEA** — IDE de desenvolvimento

---

## 🏛️ Arquitetura

O projeto segue o padrão **MVC (Model-View-Controller)** com separação estrita de camadas:

```
┌─────────────────────────────────────────┐
│           UI (JavaFX)                   │
│  HelloApplication implements View       │
│  - Captura cliques                      │
│  - Apresenta dados                      │
│  - NUNCA faz cálculos                   │
└──────────────┬──────────────────────────┘
               │
               │ via interface View
               ▼
┌─────────────────────────────────────────┐
│           Model                         │
│  BasketModel                            │
│  - Lista polimórfica de FoodItem        │
│  - Cálculos e lógica de negócio         │
│  - Notifica a View de alterações        │
└─────────────────────────────────────────┘
```

### Princípios aplicados

- **Polimorfismo puro** — zero uso de `instanceof` ou `getClass()`
- **Encapsulamento** — atributos privados e final sempre que possível
- **Observer pattern** — Model notifica View através da interface `View`
- **Code style** — identificadores em inglês, métodos curtos, Javadoc em métodos públicos

---

## 🚀 Como executar

### Pré-requisitos

- Java JDK 17 ou superior
- Maven (ou IntelliJ IDEA)
- JavaFX SDK configurado no IDE

### Passos

1. Clonar o repositório:
   ```bash
   git clone https://github.com/sanina11/smart_food_basket_tp.git
   cd smart_food_basket_tp
   ```

2. Abrir o projeto no IntelliJ IDEA

3. Garantir que o Maven descarrega as dependências do `pom.xml`

4. Executar a classe `HelloApplication` (`src/main/java/app/ui/HelloApplication.java`)

### Alterar o catálogo de alimentos

Para testar com diferentes catálogos, edita a linha do `start()`:

```java
this.basketModel.loadFromCsv("src/main/resources/foods.csv");        // 10 itens
this.basketModel.loadFromCsv("src/main/resources/foods_medium.csv"); // 20 itens
this.basketModel.loadFromCsv("src/main/resources/foods_large.csv");  // 30 itens + 5 com erros
```

---

## 📁 Estrutura do projeto

```
smart_food_basket_tp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── app/
│   │   │       ├── basketModel/
│   │   │       │   ├── FoodGroup.java      # Enum dos grupos alimentares
│   │   │       │   ├── FoodItem.java       # Classe abstrata base
│   │   │       │   ├── PackagedFood.java   # Alimentos embalados
│   │   │       │   ├── BulkFood.java       # Alimentos a granel
│   │   │       │   └── BasketModel.java    # Lógica de negócio
│   │   │       └── ui/
│   │   │           ├── View.java           # Interface de comunicação
│   │   │           └── HelloApplication.java  # Aplicação JavaFX
│   │   └── resources/
│   │       ├── foods.csv                   # Catálogo pequeno (10)
│   │       ├── foods_medium.csv            # Catálogo médio (20)
│   │       ├── foods_large.csv             # Catálogo grande (30+erros)
│   │       └── images/                     # Imagens dos alimentos
│   └── test/
│       └── java/
│           └── app/basketModel/
│               └── ModelTest.java          # Testes JUnit
├── pom.xml
├── module-info.java
├── relatorio_ia.txt
└── README.md
```

---

## 🧪 Testes

O projeto inclui quatro testes JUnit que validam o comportamento do Model:

| Teste | O que valida |
|---|---|
| `testPackagedPrice()` | Preço fixo de produtos embalados |
| `testBulkPriceCalculation()` | Cálculo proporcional para produtos a granel |
| `testTotalBasketCalculation()` | Soma polimórfica do cabaz |
| `testSDGGoalCondition()` | Validação da meta dos 400g de vegetais |

### Executar os testes

No IntelliJ, clica com o botão direito na pasta `test/` e seleciona **Run All Tests**.

---

## 🌍 ODS abrangidos

O projeto promove três Objetivos de Desenvolvimento Sustentável:

### 🍽️ ODS 2 — Fome Zero
Promove **diversidade alimentar** incentivando o utilizador a incluir alimentos de múltiplos grupos no cabaz.

### ❤️ ODS 3 — Saúde e Bem-estar
Valida uma **meta nutricional** de ingestão mínima diária de 400g de vegetais, recomendada pela Organização Mundial de Saúde.

### 🌱 ODS 12 — Consumo e Produção Responsáveis
Desafia o utilizador a escolher **produtos a granel** e a evitar embalagens de plástico, reduzindo o desperdício.

---

## 📝 Formato do ficheiro CSV

Cada linha representa um alimento com 7 campos separados por vírgula:

```
Tipo,Nome,Grupo,PrecoBase,CaloriasBase,InfoExtra,Imagem
```

### Exemplo

```csv
PACKAGED,Arroz,YELLOW,1.89,350.0,Cartao,rice.png
BULK,Brocolos,GREEN,1.20,34.0,-,broccoli.png
```

### Regras

- **Tipo** — `PACKAGED` ou `BULK`
- **Grupo** — `GREEN`, `YELLOW`, `BLUE`, `BROWN`, `RED` ou `ORANGE`
- **PrecoBase** — €/un. (embalado) ou €/kg (granel)
- **CaloriasBase** — kcal/un. (embalado) ou kcal/100g (granel)
- **InfoExtra** — material de embalagem (embalados) ou `-` (granel)
- **Imagem** — nome do ficheiro PNG em `resources/images/`

Linhas com erros de formato são automaticamente rejeitadas e reportadas ao utilizador através de um diálogo.

---

##Autores

Desenvolvido por alunos do **2.º ano** da Licenciatura em Engenharia Informática da Escola Superior de Tecnologia e Gestão do Instituto Politécnico de Beja, no âmbito da Unidade Curricular de **Programação Orientada por Objetos**.

