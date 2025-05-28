# Documentação de Testes JUnit
## Modelo de Risco de Qualidade - Banco Itaú

Este documento descreve os testes JUnit implementados para validar o modelo de risco de qualidade.

## Visão Geral dos Testes

O projeto utiliza JUnit 5 para testes unitários dos componentes principais:

1. **CalculadoraRiscoTest**: Testa a classe principal que implementa a lógica de cálculo de risco
2. **ResultadoAvaliacaoTest**: Testa a classe que representa os resultados da avaliação

## Testes da Calculadora de Risco

Os testes para a `CalculadoraRisco` cobrem as seguintes funcionalidades:

### Classificação de Critérios
- `testClassificarCriterioNormal`: Verifica a classificação correta de critérios com limiares padrão (ex: densidade de defeitos)
- `testClassificarCriterioInvertido`: Verifica a classificação correta de critérios com limiares invertidos (ex: cobertura de testes)

### Cálculo de Pontuações
- `testCalcularPontuacaoDimensao`: Verifica o cálculo correto da pontuação ponderada para uma dimensão
- `testCalcularRiscoTotalBaixoRisco`: Verifica o cálculo correto para aplicações de baixo risco
- `testCalcularRiscoTotalMedioRisco`: Verifica o cálculo correto para aplicações de médio risco
- `testCalcularRiscoTotalAltoRisco`: Verifica o cálculo correto para aplicações de alto risco

### Verificação de Vetos
- `testVerificarVetos`: Verifica a identificação correta de critérios de veto
  - Testa vetos individuais (vulnerabilidades críticas, performance ruim, bugs críticos, taxa de erro alta)
  - Testa cenários com múltiplos vetos

### Geração de Relatórios
- `testGerarRelatorioAplicacaoBaixoRiscoSemVetos`: Testa relatório para aplicações de baixo risco sem vetos
- `testGerarRelatorioAplicacaoBaixoRiscoComVetos`: Testa relatório para aplicações de baixo risco com vetos
- `testGerarRelatorioAplicacaoMedioRiscoSemVetos`: Testa relatório para aplicações de médio risco
- `testGerarRelatorioAplicacaoAltoRiscoSemVetos`: Testa relatório para aplicações de alto risco

### Caso de Teste Específico: Qube
- `testAvaliacaoQubeAICUBE`: Verifica a avaliação do Qube AICUBE
  - Testa o cenário de aprovação (taxa de erro < 1%)
  - Testa o cenário de bloqueio (taxa de erro > 1%)

## Testes do Resultado da Avaliação

Os testes para a classe `ResultadoAvaliacao` cobrem:

### Criação e Getters
- `testCriarResultadoAvaliacao`: Verifica a criação correta a partir de um relatório

### Verificações de Status
- `testIsAprovado`: Verifica a identificação correta de aprovação
- `testIsBloqueado`: Verifica a identificação correta de bloqueio
- `testIsCondicionado`: Verifica a identificação correta de aprovação condicional

### Verificações de Nível de Risco
- `testIsBaixoRisco`: Verifica a identificação correta de baixo risco
- `testIsMedioRisco`: Verifica a identificação correta de médio risco
- `testIsAltoRisco`: Verifica a identificação correta de alto risco

### Acesso a Dados
- `testGetPontuacoesDimensoes`: Verifica o acesso correto às pontuações por dimensão

## Dados de Teste

Para os testes, são criados três conjuntos de dados representando aplicações com diferentes níveis de risco:

1. **Dados de Baixo Risco**: Todos os critérios com valores de baixo risco (Ex: densidade de defeitos = 1.0)
2. **Dados de Médio Risco**: Mistura de critérios com valores de baixo e médio risco
3. **Dados de Alto Risco**: Vários critérios com valores de alto risco

Além disso, há um conjunto de dados específico que simula a avaliação do Qube AICUBE.

## Execução dos Testes

Para executar os testes, utilize o comando Maven:

```bash
mvn test
```

A saída mostrará os resultados de cada teste, incluindo informações detalhadas sobre qualquer falha.

## Cobertura de Testes

Os testes cobrem:

- 100% dos métodos públicos da `CalculadoraRisco`
- 100% dos métodos públicos da `ResultadoAvaliacao`
- Todos os cenários de classificação de risco (baixo, médio, alto)
- Todos os critérios de veto
- Casos de borda para os limiares de classificação

## Relatório de Testes

O Maven Surefire Plugin gera relatórios detalhados na pasta `target/surefire-reports`.

## Manutenção dos Testes

Ao modificar os critérios de avaliação ou limiares no modelo de risco, é necessário atualizar:

1. Os dados de teste nos métodos auxiliares 
2. Os valores esperados nos asserts correspondentes
3. Os testes de limites para refletir os novos limiares