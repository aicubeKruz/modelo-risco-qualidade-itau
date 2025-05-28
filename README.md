# Modelo de Risco de Qualidade para Aplicações Bancárias
## Banco Itaú - Governança de Tecnologia

[![Status](https://img.shields.io/badge/Status-Stable-green.svg)](https://github.com/aicubeKruz/modelo-risco-qualidade-itau)
[![License](https://img.shields.io/badge/License-Proprietary-blue.svg)](https://github.com/aicubeKruz/modelo-risco-qualidade-itau)
[![JUnit](https://img.shields.io/badge/Tests-JUnit_5-red.svg)](https://github.com/aicubeKruz/modelo-risco-qualidade-itau)

Este projeto implementa um modelo de avaliação de risco para aplicações bancárias, focando na qualidade do software em quatro dimensões críticas:

- **Bugs**: Defeitos que comprometem a funcionalidade da aplicação
- **Performance**: Desempenho e tempo de resposta da aplicação
- **Segurança**: Proteção de dados e resistência a ataques
- **Experiência**: Usabilidade e satisfação do cliente

O modelo estabelece critérios de avaliação, níveis de risco e mecanismos de gate que impedem o avanço de aplicações com qualidade inadequada para o ambiente de produção.

## Estrutura do Projeto

```
modelo-risco-qualidade-itau/
├── src/
│   ├── main/java/br/com/itau/governanca/risco/
│   │   ├── CalculadoraRisco.java     # Implementação principal do modelo
│   │   └── ResultadoAvaliacao.java   # Representação dos resultados
│   └── test/java/br/com/itau/governanca/risco/
│       ├── CalculadoraRiscoTest.java # Testes da calculadora
│       └── ResultadoAvaliacaoTest.java # Testes dos resultados
├── documentacao/
│   └── relatorios/
│       ├── modelo_risco_qualidade_itau.md  # Definição do modelo de risco
│       ├── calculadora_risco_qualidade.csv # Template para cálculo de risco
│       ├── template_gate_qualidade.md      # Template para gates de qualidade
│       ├── avaliacao_qube.csv              # Exemplo de avaliação do Qube
│       ├── gate_qualidade_qube.md          # Relatório de gate do Qube
│       ├── dashboard_qube.txt              # Dashboard visual do Qube
│       └── relatorio_qube*.json            # Relatórios gerados pela avaliação
└── pom.xml                                 # Configurações do Maven
```

## Funcionalidades

- Avaliação de risco baseada em múltiplos critérios
- Classificação em três níveis: Verde (Baixo), Amarelo (Médio), Vermelho (Alto)
- Identificação automática de critérios de veto que bloqueiam a liberação
- Geração de relatórios detalhados de avaliação
- Suporte a decisões condicionais para riscos médios

## Como Utilizar

### Requisitos

- Java 11 ou superior
- Maven 3.6 ou superior

### Compilação e Testes

```bash
# Compilar o projeto
mvn clean compile

# Executar os testes
mvn test
```

### Exemplo de Uso

```java
// Criar a calculadora de risco
CalculadoraRisco calculadora = new CalculadoraRisco();

// Preparar os dados da aplicação a ser avaliada
Map<String, Map<String, Double>> dados = new HashMap<>();
// Preencher os dados com métricas da aplicação...

// Avaliar a aplicação
Map<String, Object> relatorio = calculadora.gerarRelatorio("Nome da App", "1.0", dados);
ResultadoAvaliacao resultado = new ResultadoAvaliacao(relatorio);

// Verificar o resultado
if (resultado.isAprovado()) {
    System.out.println("Aplicação aprovada para produção!");
} else if (resultado.isBloqueado()) {
    System.out.println("Aplicação bloqueada devido a critérios de veto: " + 
                       resultado.getVetos());
} else {
    System.out.println("Aplicação aprovada condicionalmente com risco: " + 
                       resultado.getClassificacao());
}
```

## Dimensões de Qualidade e Critérios

### Bugs (Estabilidade Funcional)
- Densidade de defeitos
- Cobertura de testes
- Bugs críticos pendentes
- Taxa de regressão
- MTBF (Mean Time Between Failures)

### Performance
- Tempo de resposta
- Percentil 95 de tempo de resposta
- Utilização de recursos
- Escalabilidade
- Tempo de inicialização
- Throughput

### Segurança
- Vulnerabilidades críticas
- Vulnerabilidades totais
- OWASP Top 10
- Segurança de dados
- Autenticação/Autorização
- Pentest

### Experiência do Cliente
- Satisfação do usuário
- Taxa de erro do usuário
- Tempo de conclusão de tarefa
- Acessibilidade
- Taxa de abandono
- Adoção de funcionalidades

## Critérios de Veto

Qualquer um dos seguintes critérios impede automaticamente a promoção para produção:

1. Qualquer vulnerabilidade crítica de segurança não mitigada
2. Tempo de resposta P95 > 3000ms em operações críticas
3. Bugs que impeçam fluxo principal do cliente
4. Taxa de erro em produção >1% para operações críticas

## Licença

Este projeto é de propriedade do Banco Itaú e seu uso está restrito às políticas internas da organização.

## Contribuições

Desenvolvido pela área de Governança de Tecnologia do Banco Itaú.