package br.com.itau.governanca.risco;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculadora de Risco de Qualidade para aplicações bancárias
 * Implementa o modelo de risco para avaliar a qualidade de aplicações
 * baseado em quatro dimensões: bugs, performance, segurança e experiência
 */
public class CalculadoraRisco {
    
    // Pesos das dimensões
    private final Map<String, Double> pesosDimensoes;
    
    // Pesos dos critérios por dimensão
    private final Map<String, Map<String, Double>> pesosCriterios;
    
    // Limiares para classificação de risco
    private final Map<String, Map<String, Map<String, Object>>> limiares;
    
    /**
     * Construtor que inicializa os pesos e limiares do modelo de risco
     */
    public CalculadoraRisco() {
        // Inicializa os pesos das dimensões
        pesosDimensoes = new HashMap<>();
        pesosDimensoes.put("bugs", 0.25);
        pesosDimensoes.put("performance", 0.25);
        pesosDimensoes.put("seguranca", 0.30);
        pesosDimensoes.put("experiencia", 0.20);
        
        // Inicializa os pesos dos critérios
        pesosCriterios = new HashMap<>();
        
        // Pesos para Bugs
        Map<String, Double> pesosBugs = new HashMap<>();
        pesosBugs.put("densidade_defeitos", 0.25);
        pesosBugs.put("cobertura_testes", 0.20);
        pesosBugs.put("bugs_criticos", 0.30);
        pesosBugs.put("taxa_regressao", 0.15);
        pesosBugs.put("mtbf", 0.10);
        pesosCriterios.put("bugs", pesosBugs);
        
        // Pesos para Performance
        Map<String, Double> pesosPerformance = new HashMap<>();
        pesosPerformance.put("tempo_resposta", 0.25);
        pesosPerformance.put("percentil_95", 0.25);
        pesosPerformance.put("utilizacao_recursos", 0.15);
        pesosPerformance.put("escalabilidade", 0.15);
        pesosPerformance.put("tempo_inicializacao", 0.05);
        pesosPerformance.put("throughput", 0.15);
        pesosCriterios.put("performance", pesosPerformance);
        
        // Pesos para Segurança
        Map<String, Double> pesosSeguranca = new HashMap<>();
        pesosSeguranca.put("vulnerabilidades_criticas", 0.30);
        pesosSeguranca.put("vulnerabilidades_totais", 0.20);
        pesosSeguranca.put("owasp_top10", 0.20);
        pesosSeguranca.put("seguranca_dados", 0.15);
        pesosSeguranca.put("autenticacao_autorizacao", 0.10);
        pesosSeguranca.put("pentest", 0.05);
        pesosCriterios.put("seguranca", pesosSeguranca);
        
        // Pesos para Experiência
        Map<String, Double> pesosExperiencia = new HashMap<>();
        pesosExperiencia.put("satisfacao_usuario", 0.25);
        pesosExperiencia.put("taxa_erro_usuario", 0.20);
        pesosExperiencia.put("tempo_conclusao_tarefa", 0.15);
        pesosExperiencia.put("acessibilidade", 0.10);
        pesosExperiencia.put("taxa_abandono", 0.15);
        pesosExperiencia.put("adocao_funcionalidades", 0.15);
        pesosCriterios.put("experiencia", pesosExperiencia);
        
        // Inicializa os limiares
        limiares = new HashMap<>();
        
        // Limiares para Bugs
        Map<String, Map<String, Object>> limBugs = new HashMap<>();
        
        Map<String, Object> limDensidadeDefeitos = new HashMap<>();
        limDensidadeDefeitos.put("baixo", 2.0);
        limDensidadeDefeitos.put("medio", 5.0);
        limBugs.put("densidade_defeitos", limDensidadeDefeitos);
        
        Map<String, Object> limCoberturaTestes = new HashMap<>();
        limCoberturaTestes.put("baixo", 90.0);
        limCoberturaTestes.put("medio", 70.0);
        limCoberturaTestes.put("invertido", true);
        limBugs.put("cobertura_testes", limCoberturaTestes);
        
        Map<String, Object> limBugsCriticos = new HashMap<>();
        limBugsCriticos.put("baixo", 0.0);
        limBugsCriticos.put("medio", 2.0);
        limBugs.put("bugs_criticos", limBugsCriticos);
        
        Map<String, Object> limTaxaRegressao = new HashMap<>();
        limTaxaRegressao.put("baixo", 1.0);
        limTaxaRegressao.put("medio", 3.0);
        limBugs.put("taxa_regressao", limTaxaRegressao);
        
        Map<String, Object> limMTBF = new HashMap<>();
        limMTBF.put("baixo", 720.0);
        limMTBF.put("medio", 168.0);
        limMTBF.put("invertido", true);
        limBugs.put("mtbf", limMTBF);
        
        limiares.put("bugs", limBugs);
        
        // Limiares para Performance
        Map<String, Map<String, Object>> limPerformance = new HashMap<>();
        
        Map<String, Object> limTempoResposta = new HashMap<>();
        limTempoResposta.put("baixo", 300.0);
        limTempoResposta.put("medio", 800.0);
        limPerformance.put("tempo_resposta", limTempoResposta);
        
        Map<String, Object> limPercentil95 = new HashMap<>();
        limPercentil95.put("baixo", 800.0);
        limPercentil95.put("medio", 2000.0);
        limPerformance.put("percentil_95", limPercentil95);
        
        Map<String, Object> limUtilizacaoRecursos = new HashMap<>();
        limUtilizacaoRecursos.put("baixo", 60.0);
        limUtilizacaoRecursos.put("medio", 85.0);
        limPerformance.put("utilizacao_recursos", limUtilizacaoRecursos);
        
        Map<String, Object> limEscalabilidade = new HashMap<>();
        limEscalabilidade.put("baixo", 10.0);
        limEscalabilidade.put("medio", 30.0);
        limPerformance.put("escalabilidade", limEscalabilidade);
        
        Map<String, Object> limTempoInicializacao = new HashMap<>();
        limTempoInicializacao.put("baixo", 5.0);
        limTempoInicializacao.put("medio", 15.0);
        limPerformance.put("tempo_inicializacao", limTempoInicializacao);
        
        Map<String, Object> limThroughput = new HashMap<>();
        limThroughput.put("baixo", 100.0);
        limThroughput.put("medio", 50.0);
        limThroughput.put("invertido", true);
        limPerformance.put("throughput", limThroughput);
        
        limiares.put("performance", limPerformance);
        
        // Limiares para Segurança
        Map<String, Map<String, Object>> limSeguranca = new HashMap<>();
        
        Map<String, Object> limVulnCriticas = new HashMap<>();
        limVulnCriticas.put("baixo", 0.0);
        limVulnCriticas.put("medio", 1.0);
        limSeguranca.put("vulnerabilidades_criticas", limVulnCriticas);
        
        Map<String, Object> limVulnTotais = new HashMap<>();
        limVulnTotais.put("baixo", 5.0);
        limVulnTotais.put("medio", 15.0);
        limSeguranca.put("vulnerabilidades_totais", limVulnTotais);
        
        Map<String, Object> limOwaspTop10 = new HashMap<>();
        limOwaspTop10.put("baixo", 100.0);
        limOwaspTop10.put("medio", 90.0);
        limOwaspTop10.put("invertido", true);
        limSeguranca.put("owasp_top10", limOwaspTop10);
        
        Map<String, Object> limSegurancaDados = new HashMap<>();
        limSegurancaDados.put("baixo", 100.0);
        limSegurancaDados.put("medio", 95.0);
        limSegurancaDados.put("invertido", true);
        limSeguranca.put("seguranca_dados", limSegurancaDados);
        
        Map<String, Object> limAutenticacao = new HashMap<>();
        limAutenticacao.put("baixo", 2.0);
        limAutenticacao.put("medio", 1.0);
        limSeguranca.put("autenticacao_autorizacao", limAutenticacao);
        
        Map<String, Object> limPentest = new HashMap<>();
        limPentest.put("baixo", 2.0);
        limPentest.put("medio", 1.0);
        limSeguranca.put("pentest", limPentest);
        
        limiares.put("seguranca", limSeguranca);
        
        // Limiares para Experiência
        Map<String, Map<String, Object>> limExperiencia = new HashMap<>();
        
        Map<String, Object> limSatisfacaoUsuario = new HashMap<>();
        limSatisfacaoUsuario.put("baixo", 70.0);
        limSatisfacaoUsuario.put("medio", 40.0);
        limSatisfacaoUsuario.put("invertido", true);
        limExperiencia.put("satisfacao_usuario", limSatisfacaoUsuario);
        
        Map<String, Object> limTaxaErroUsuario = new HashMap<>();
        limTaxaErroUsuario.put("baixo", 2.0);
        limTaxaErroUsuario.put("medio", 5.0);
        limExperiencia.put("taxa_erro_usuario", limTaxaErroUsuario);
        
        Map<String, Object> limTempoConclusaoTarefa = new HashMap<>();
        limTempoConclusaoTarefa.put("baixo", 10.0);
        limTempoConclusaoTarefa.put("medio", 30.0);
        limExperiencia.put("tempo_conclusao_tarefa", limTempoConclusaoTarefa);
        
        Map<String, Object> limAcessibilidade = new HashMap<>();
        limAcessibilidade.put("baixo", 95.0);
        limAcessibilidade.put("medio", 80.0);
        limAcessibilidade.put("invertido", true);
        limExperiencia.put("acessibilidade", limAcessibilidade);
        
        Map<String, Object> limTaxaAbandono = new HashMap<>();
        limTaxaAbandono.put("baixo", 5.0);
        limTaxaAbandono.put("medio", 15.0);
        limExperiencia.put("taxa_abandono", limTaxaAbandono);
        
        Map<String, Object> limAdocaoFuncionalidades = new HashMap<>();
        limAdocaoFuncionalidades.put("baixo", 60.0);
        limAdocaoFuncionalidades.put("medio", 30.0);
        limAdocaoFuncionalidades.put("invertido", true);
        limExperiencia.put("adocao_funcionalidades", limAdocaoFuncionalidades);
        
        limiares.put("experiencia", limExperiencia);
    }
    
    /**
     * Classifica um critério como baixo (1), médio (2) ou alto (3) risco
     * @param dimensao A dimensão do critério (bugs, performance, seguranca, experiencia)
     * @param criterio O nome do critério
     * @param valor O valor a ser classificado
     * @return A classificação de risco (1=baixo, 2=médio, 3=alto)
     */
    public int classificarCriterio(String dimensao, String criterio, double valor) {
        Map<String, Object> limiar = limiares.get(dimensao).get(criterio);
        boolean invertido = limiar.containsKey("invertido") ? (boolean) limiar.get("invertido") : false;
        double limiarBaixo = (double) limiar.get("baixo");
        double limiarMedio = (double) limiar.get("medio");
        
        if (invertido) {
            if (valor >= limiarBaixo) {
                return 1; // Baixo risco
            } else if (valor >= limiarMedio) {
                return 2; // Médio risco
            } else {
                return 3; // Alto risco
            }
        } else {
            if (valor <= limiarBaixo) {
                return 1; // Baixo risco
            } else if (valor <= limiarMedio) {
                return 2; // Médio risco
            } else {
                return 3; // Alto risco
            }
        }
    }
    
    /**
     * Calcula a pontuação ponderada para uma dimensão
     * @param dimensao A dimensão (bugs, performance, seguranca, experiencia)
     * @param valores Um mapa com os valores dos critérios
     * @return A pontuação ponderada da dimensão
     */
    public double calcularPontuacaoDimensao(String dimensao, Map<String, Double> valores) {
        double pontuacaoTotal = 0;
        double pesoTotal = 0;
        
        Map<String, Double> pesosCriterioDimensao = pesosCriterios.get(dimensao);
        
        for (Map.Entry<String, Double> entry : valores.entrySet()) {
            String criterio = entry.getKey();
            double valor = entry.getValue();
            
            if (pesosCriterioDimensao.containsKey(criterio)) {
                double peso = pesosCriterioDimensao.get(criterio);
                int classificacao = classificarCriterio(dimensao, criterio, valor);
                pontuacaoTotal += classificacao * peso;
                pesoTotal += peso;
            }
        }
        
        // Garantir que temos pesos válidos
        if (pesoTotal == 0) {
            return 0;
        }
        
        return pontuacaoTotal / pesoTotal;
    }
    
    /**
     * Calcula o risco total com base nas pontuações das dimensões
     * @param dados Um mapa com os valores para todas as dimensões e critérios
     * @return Um mapa com o resultado da avaliação de risco
     */
    public Map<String, Object> calcularRiscoTotal(Map<String, Map<String, Double>> dados) {
        Map<String, Double> pontuacoesDimensoes = new HashMap<>();
        double riscoTotal = 0;
        
        for (Map.Entry<String, Map<String, Double>> entry : dados.entrySet()) {
            String dimensao = entry.getKey();
            Map<String, Double> criterios = entry.getValue();
            
            double pontuacao = calcularPontuacaoDimensao(dimensao, criterios);
            pontuacoesDimensoes.put(dimensao, pontuacao);
            riscoTotal += pontuacao * pesosDimensoes.get(dimensao);
        }
        
        // Classificação final
        String classificacao;
        if (riscoTotal <= 1.5) {
            classificacao = "VERDE (Baixo Risco)";
        } else if (riscoTotal <= 2.2) {
            classificacao = "AMARELO (Médio Risco)";
        } else {
            classificacao = "VERMELHO (Alto Risco)";
        }
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("pontuacao_total", riscoTotal);
        resultado.put("classificacao", classificacao);
        resultado.put("pontuacoes_dimensoes", pontuacoesDimensoes);
        
        return resultado;
    }
    
    /**
     * Verifica se algum dos critérios de veto foi atingido
     * @param dados Um mapa com os valores para todas as dimensões e critérios
     * @return Uma lista com os critérios de veto atingidos
     */
    public List<String> verificarVetos(Map<String, Map<String, Double>> dados) {
        List<String> vetos = new ArrayList<>();
        
        // Veto 1: Qualquer vulnerabilidade crítica de segurança não mitigada
        if (dados.containsKey("seguranca") && 
            dados.get("seguranca").containsKey("vulnerabilidades_criticas") && 
            dados.get("seguranca").get("vulnerabilidades_criticas") > 0) {
            vetos.add("Vulnerabilidade crítica de segurança não mitigada");
        }
        
        // Veto 2: Tempo de resposta P95 > 3000ms em operações críticas
        if (dados.containsKey("performance") && 
            dados.get("performance").containsKey("percentil_95") && 
            dados.get("performance").get("percentil_95") > 3000) {
            vetos.add("Tempo de resposta P95 > 3000ms em operações críticas");
        }
        
        // Veto 3: Bugs que impeçam fluxo principal do cliente
        if (dados.containsKey("bugs") && 
            dados.get("bugs").containsKey("bugs_criticos") && 
            dados.get("bugs").get("bugs_criticos") > 0) {
            vetos.add("Bugs que impedem fluxo principal do cliente");
        }
        
        // Veto 4: Taxa de erro em produção >1% para operações críticas
        if (dados.containsKey("experiencia") && 
            dados.get("experiencia").containsKey("taxa_erro_usuario") && 
            dados.get("experiencia").get("taxa_erro_usuario") > 1) {
            vetos.add("Taxa de erro em produção >1% para operações críticas");
        }
        
        return vetos;
    }
    
    /**
     * Gera um relatório detalhado com o resultado da avaliação
     * @param nomeAplicacao Nome da aplicação
     * @param versao Versão da aplicação
     * @param dados Dados para avaliação
     * @return Um mapa com o relatório detalhado
     */
    public Map<String, Object> gerarRelatorio(String nomeAplicacao, String versao, 
                                             Map<String, Map<String, Double>> dados) {
        Map<String, Object> resultado = calcularRiscoTotal(dados);
        List<String> vetos = verificarVetos(dados);
        
        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("nome_aplicacao", nomeAplicacao);
        relatorio.put("versao", versao);
        relatorio.put("resultado", resultado);
        relatorio.put("vetos", vetos);
        
        boolean aprovado = vetos.isEmpty();
        boolean baixoRisco = resultado.get("classificacao").toString().startsWith("VERDE");
        
        String status;
        if (!aprovado) {
            status = "BLOQUEADO";
        } else if (baixoRisco) {
            status = "APROVADO";
        } else {
            status = "CONDICIONADO";
        }
        
        relatorio.put("status", status);
        relatorio.put("dados_detalhados", dados);
        
        return relatorio;
    }
}