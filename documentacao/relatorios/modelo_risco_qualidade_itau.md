# Modelo de Risco de Qualidade para Aplicações Bancárias
## Banco Itaú - Governança de Tecnologia

## 1. Introdução

Este documento define um modelo de risco para avaliação da qualidade de aplicações do Banco Itaú, visando zerar o impacto ao cliente correntista em quatro dimensões críticas:

- **Bugs**: Defeitos que comprometem a funcionalidade da aplicação
- **Performance**: Desempenho e tempo de resposta da aplicação
- **Segurança**: Proteção de dados e resistência a ataques
- **Experiência**: Usabilidade e satisfação do cliente

O modelo estabelece critérios de avaliação, níveis de risco e mecanismos de gate que impedem o avanço de aplicações com qualidade inadequada para o ambiente de produção.

## 2. Dimensões de Qualidade e Critérios de Avaliação

### 2.1. Bugs (Estabilidade Funcional)

| Critério | Métrica | Baixo Risco | Médio Risco | Alto Risco |
|----------|---------|-------------|-------------|------------|
| Densidade de defeitos | Bugs/1000 linhas de código | <2 | 2-5 | >5 |
| Cobertura de testes | % do código coberto por testes | >90% | 70-90% | <70% |
| Bugs críticos pendentes | Número absoluto | 0 | 1-2 | >2 |
| Taxa de regressão | % de funcionalidades com regressão | <1% | 1-3% | >3% |
| MTBF (Mean Time Between Failures) | Horas de operação contínua | >720h | 168-720h | <168h |

### 2.2. Performance

| Critério | Métrica | Baixo Risco | Médio Risco | Alto Risco |
|----------|---------|-------------|-------------|------------|
| Tempo de resposta | Média (milissegundos) | <300ms | 300-800ms | >800ms |
| Percentil 95 de tempo de resposta | P95 (milissegundos) | <800ms | 800-2000ms | >2000ms |
| Utilização de recursos | % CPU/Memória em pico | <60% | 60-85% | >85% |
| Escalabilidade | % degradação com 2x usuários | <10% | 10-30% | >30% |
| Tempo de inicialização | Segundos | <5s | 5-15s | >15s |
| Throughput | Transações por segundo | >100 | 50-100 | <50 |

### 2.3. Segurança

| Critério | Métrica | Baixo Risco | Médio Risco | Alto Risco |
|----------|---------|-------------|-------------|------------|
| Vulnerabilidades críticas | Número absoluto | 0 | 1 | >1 |
| Vulnerabilidades totais | Número ponderado | <5 | 5-15 | >15 |
| OWASP Top 10 | Cobertura de mitigação | 100% | 90-99% | <90% |
| Segurança de dados | % dados sensíveis protegidos | 100% | 95-99% | <95% |
| Autenticação/Autorização | Nível de conformidade | Completo | Parcial | Mínimo |
| Pentest | Resultados | Sem falhas críticas | Falhas de médio risco | Falhas de alto risco |

### 2.4. Experiência do Cliente

| Critério | Métrica | Baixo Risco | Médio Risco | Alto Risco |
|----------|---------|-------------|-------------|------------|
| Satisfação do usuário | NPS (Net Promoter Score) | >70 | 40-70 | <40 |
| Taxa de erro do usuário | % de tarefas com erro | <2% | 2-5% | >5% |
| Tempo de conclusão de tarefa | % acima do esperado | <10% | 10-30% | >30% |
| Acessibilidade | % conformidade com WCAG | >95% | 80-95% | <80% |
| Taxa de abandono | % sessões abandonadas | <5% | 5-15% | >15% |
| Adoção de funcionalidades | % uso de features novas | >60% | 30-60% | <30% |

## 3. Cálculo do Nível de Risco

### 3.1. Pontuação por Dimensão

Cada critério recebe uma pontuação:
- Baixo Risco: 1 ponto
- Médio Risco: 2 pontos
- Alto Risco: 3 pontos

A pontuação da dimensão é a média ponderada dos critérios, onde:
- Bugs: Peso 25%
- Performance: Peso 25%
- Segurança: Peso 30%
- Experiência: Peso 20%

### 3.2. Classificação Final de Risco

O risco total é calculado pela soma ponderada das dimensões:

| Classificação | Pontuação | Descrição |
|---------------|-----------|-----------|
| Verde (Baixo Risco) | 1.0 - 1.5 | Aplicação pronta para produção |
| Amarelo (Médio Risco) | 1.6 - 2.2 | Liberação condicionada a plano de mitigação |
| Vermelho (Alto Risco) | 2.3 - 3.0 | Bloqueado para produção |

## 4. Mecanismos de Gate por Risco de Qualidade

### 4.1. Gates Obrigatórios

| Fase do Ciclo | Gate | Critério de Passagem | Responsável |
|---------------|------|----------------------|-------------|
| Desenvolvimento | G1: Code Review | Sem bugs críticos, cobertura >70% | Tech Lead |
| Testes | G2: Testes Funcionais | Sem bugs críticos, >90% casos de teste passando | QA Lead |
| Pré-Produção | G3: Testes de Performance | Tempos de resposta aceitáveis, sem degradação | Eng. Performance |
| Pré-Produção | G4: Avaliação de Segurança | Sem vulnerabilidades críticas | Time de Segurança |
| Pré-Produção | G5: Testes de Experiência | NPS aceitável, taxa de erro <5% | UX Lead |
| Produção | G6: Monitoramento Inicial | KPIs estáveis por 24h após deploy | SRE |

### 4.2. Critérios de Veto (Bloqueadores Absolutos)

Qualquer um dos seguintes critérios impede automaticamente a promoção para produção:

1. Qualquer vulnerabilidade crítica de segurança não mitigada
2. Tempo de resposta P95 > 3000ms em operações críticas
3. Bugs que impeçam fluxo principal do cliente
4. Taxa de erro em produção >1% para operações críticas
5. Falha em validação de conformidade regulatória

### 4.3. Processo de Exceção

Em casos excepcionais, uma aplicação com classificação Amarela pode avançar mediante:

1. Aprovação formal do Comitê de Governança de TI
2. Plano de mitigação documentado com prazos definidos
3. Monitoramento intensivo nas primeiras 72h
4. Plano de rollback imediato documentado e testado

## 5. Implementação e Automação

### 5.1. Ferramentas de Suporte

- **SonarQube**: Análise estática de código e bugs
- **JMeter/Gatling**: Testes de performance
- **OWASP ZAP**: Análise de segurança
- **Selenium/Cypress**: Testes de interface
- **Datadog/New Relic**: Monitoramento de produção
- **Jira**: Gestão de bugs e issues
- **Jenkins/Azure DevOps**: Pipeline com gates automatizados

### 5.2. Dashboard de Qualidade

Um dashboard centralizado exibirá indicadores em tempo real para todas as aplicações:

- Status atual (Verde/Amarelo/Vermelho)
- Tendência histórica de qualidade
- Principais problemas e riscos
- KPIs por dimensão
- Controle de exceções aprovadas

## 6. Governança e Responsabilidades

### 6.1. Papéis e Responsabilidades

- **Comitê de Governança de TI**: Aprovação de exceções, revisão trimestral do modelo
- **Squad de Qualidade**: Definição e evolução dos critérios, suporte técnico
- **DevSecOps**: Automação dos gates no pipeline CI/CD
- **Líderes de Produto**: Priorização de correções de qualidade no backlog
- **SRE**: Monitoramento contínuo dos indicadores em produção

### 6.2. Ciclo de Melhoria Contínua

- Revisão mensal dos critérios e limiares
- Análise trimestral da eficácia do modelo
- Ajuste anual completo do modelo com base em dados históricos
- Incorporação de feedbacks dos clientes e stakeholders

## 7. Glossário e Referências

### 7.1. Glossário

- **NPS**: Net Promoter Score - métrica de satisfação do cliente
- **MTBF**: Mean Time Between Failures - tempo médio entre falhas
- **WCAG**: Web Content Accessibility Guidelines
- **OWASP**: Open Web Application Security Project
- **P95**: Percentil 95 (valor abaixo do qual estão 95% das medições)

### 7.2. Referências

- ISO 25010: Modelo de Qualidade de Software
- ITIL 4: Práticas de Gestão de Serviços
- COBIT 2019: Framework de Governança de TI
- CMMI: Capability Maturity Model Integration
- Resolução BCB nº 4.658: Requisitos de Segurança Cibernética

## 8. Controle de Versão do Documento

| Versão | Data | Autor | Descrição das Alterações |
|--------|------|-------|--------------------------|
| 1.0 | 26/05/2025 | Governança de TI | Versão inicial |
