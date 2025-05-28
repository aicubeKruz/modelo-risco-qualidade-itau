package br.com.itau.governanca.risco;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Testes unitários para a CalculadoraRisco
 */
public class CalculadoraRiscoTest {
    
    private CalculadoraRisco calculadora;
    
    @BeforeEach
    public void setUp() {
        calculadora = new CalculadoraRisco();
    }
    
    @Test
    @DisplayName("Deve classificar corretamente critérios com limiares normais")
    public void testClassificarCriterioNormal() {
        // Densidade de defeitos (limiar baixo = 2, médio = 5)
        assertEquals(1, calculadora.classificarCriterio("bugs", "densidade_defeitos", 1.5));
        assertEquals(1, calculadora.classificarCriterio("bugs", "densidade_defeitos", 2.0));
        assertEquals(2, calculadora.classificarCriterio("bugs", "densidade_defeitos", 3.5));
        assertEquals(2, calculadora.classificarCriterio("bugs", "densidade_defeitos", 5.0));
        assertEquals(3, calculadora.classificarCriterio("bugs", "densidade_defeitos", 5.1));
    }
    
    @Test
    @DisplayName("Deve classificar corretamente critérios com limiares invertidos")
    public void testClassificarCriterioInvertido() {
        // Cobertura de testes (limiar baixo = 90, médio = 70, invertido = true)
        assertEquals(1, calculadora.classificarCriterio("bugs", "cobertura_testes", 95.0));
        assertEquals(1, calculadora.classificarCriterio("bugs", "cobertura_testes", 90.0));
        assertEquals(2, calculadora.classificarCriterio("bugs", "cobertura_testes", 80.0));
        assertEquals(2, calculadora.classificarCriterio("bugs", "cobertura_testes", 70.0));
        assertEquals(3, calculadora.classificarCriterio("bugs", "cobertura_testes", 69.9));
    }
    
    @Test
    @DisplayName("Deve calcular corretamente a pontuação de uma dimensão")
    public void testCalcularPontuacaoDimensao() {
        Map<String, Double> dadosBugs = new HashMap<>();
        dadosBugs.put("densidade_defeitos", 1.0);  // Classificação: 1
        dadosBugs.put("cobertura_testes", 95.0);   // Classificação: 1
        dadosBugs.put("bugs_criticos", 0.0);       // Classificação: 1
        dadosBugs.put("taxa_regressao", 0.5);      // Classificação: 1
        dadosBugs.put("mtbf", 800.0);              // Classificação: 1
        
        double pontuacao = calculadora.calcularPontuacaoDimensao("bugs", dadosBugs);
        assertEquals(1.0, pontuacao, 0.001);
        
        // Altera alguns valores para classificação média
        dadosBugs.put("densidade_defeitos", 3.0);  // Classificação: 2
        dadosBugs.put("cobertura_testes", 80.0);   // Classificação: 2
        
        pontuacao = calculadora.calcularPontuacaoDimensao("bugs", dadosBugs);
        // Média ponderada: (2*0.25 + 2*0.20 + 1*0.30 + 1*0.15 + 1*0.10) = 1.45
        assertEquals(1.45, pontuacao, 0.001);
    }
    
    @Test
    @DisplayName("Deve calcular corretamente o risco total com todas as dimensões em baixo risco")
    public void testCalcularRiscoTotalBaixoRisco() {
        Map<String, Map<String, Double>> dados = criarDadosBaixoRisco();
        
        Map<String, Object> resultado = calculadora.calcularRiscoTotal(dados);
        
        assertEquals(1.0, (double) resultado.get("pontuacao_total"), 0.001);
        assertEquals("VERDE (Baixo Risco)", resultado.get("classificacao"));
        
        @SuppressWarnings("unchecked")
        Map<String, Double> pontuacoesDimensoes = (Map<String, Double>) resultado.get("pontuacoes_dimensoes");
        assertEquals(1.0, pontuacoesDimensoes.get("bugs"), 0.001);
        assertEquals(1.0, pontuacoesDimensoes.get("performance"), 0.001);
        assertEquals(1.0, pontuacoesDimensoes.get("seguranca"), 0.001);
        assertEquals(1.0, pontuacoesDimensoes.get("experiencia"), 0.001);
    }
    
    @Test
    @DisplayName("Deve calcular corretamente o risco total com algumas dimensões em médio risco")
    public void testCalcularRiscoTotalMedioRisco() {
        Map<String, Map<String, Double>> dados = criarDadosMedioRisco();
        
        Map<String, Object> resultado = calculadora.calcularRiscoTotal(dados);
        
        // Pontuação esperada: 0.25*1.5 + 0.25*2.0 + 0.30*1.8 + 0.20*2.2 = 1.84
        assertEquals(1.84, (double) resultado.get("pontuacao_total"), 0.01);
        assertEquals("AMARELO (Médio Risco)", resultado.get("classificacao"));
    }
    
    @Test
    @DisplayName("Deve calcular corretamente o risco total com algumas dimensões em alto risco")
    public void testCalcularRiscoTotalAltoRisco() {
        Map<String, Map<String, Double>> dados = criarDadosAltoRisco();
        
        Map<String, Object> resultado = calculadora.calcularRiscoTotal(dados);
        
        // Pontuação esperada: 0.25*2.0 + 0.25*2.5 + 0.30*3.0 + 0.20*2.3 = 2.51
        assertEquals(2.51, (double) resultado.get("pontuacao_total"), 0.01);
        assertEquals("VERMELHO (Alto Risco)", resultado.get("classificacao"));
    }
    
    @Test
    @DisplayName("Deve identificar corretamente critérios de veto")
    public void testVerificarVetos() {
        // Dados sem vetos
        Map<String, Map<String, Double>> dadosSemVetos = criarDadosBaixoRisco();
        List<String> vetos = calculadora.verificarVetos(dadosSemVetos);
        assertTrue(vetos.isEmpty());
        
        // Vulnerabilidade crítica
        Map<String, Map<String, Double>> dadosComVulnCritica = criarDadosBaixoRisco();
        dadosComVulnCritica.get("seguranca").put("vulnerabilidades_criticas", 1.0);
        vetos = calculadora.verificarVetos(dadosComVulnCritica);
        assertEquals(1, vetos.size());
        assertTrue(vetos.contains("Vulnerabilidade crítica de segurança não mitigada"));
        
        // Tempo de resposta P95 alto
        Map<String, Map<String, Double>> dadosComP95Alto = criarDadosBaixoRisco();
        dadosComP95Alto.get("performance").put("percentil_95", 3500.0);
        vetos = calculadora.verificarVetos(dadosComP95Alto);
        assertEquals(1, vetos.size());
        assertTrue(vetos.contains("Tempo de resposta P95 > 3000ms em operações críticas"));
        
        // Bugs críticos
        Map<String, Map<String, Double>> dadosComBugCritico = criarDadosBaixoRisco();
        dadosComBugCritico.get("bugs").put("bugs_criticos", 1.0);
        vetos = calculadora.verificarVetos(dadosComBugCritico);
        assertEquals(1, vetos.size());
        assertTrue(vetos.contains("Bugs que impedem fluxo principal do cliente"));
        
        // Taxa de erro alto
        Map<String, Map<String, Double>> dadosComTaxaErroAlta = criarDadosBaixoRisco();
        dadosComTaxaErroAlta.get("experiencia").put("taxa_erro_usuario", 1.5);
        vetos = calculadora.verificarVetos(dadosComTaxaErroAlta);
        assertEquals(1, vetos.size());
        assertTrue(vetos.contains("Taxa de erro em produção >1% para operações críticas"));
        
        // Múltiplos vetos
        Map<String, Map<String, Double>> dadosComMultiplosVetos = criarDadosBaixoRisco();
        dadosComMultiplosVetos.get("seguranca").put("vulnerabilidades_criticas", 1.0);
        dadosComMultiplosVetos.get("bugs").put("bugs_criticos", 2.0);
        vetos = calculadora.verificarVetos(dadosComMultiplosVetos);
        assertEquals(2, vetos.size());
    }
    
    @Test
    @DisplayName("Deve gerar relatório correto para aplicação de baixo risco sem vetos")
    public void testGerarRelatorioAplicacaoBaixoRiscoSemVetos() {
        Map<String, Map<String, Double>> dados = criarDadosBaixoRisco();
        
        Map<String, Object> relatorio = calculadora.gerarRelatorio("Aplicacao Teste", "1.0", dados);
        
        assertEquals("Aplicacao Teste", relatorio.get("nome_aplicacao"));
        assertEquals("1.0", relatorio.get("versao"));
        assertEquals("APROVADO", relatorio.get("status"));
        
        @SuppressWarnings("unchecked")
        List<String> vetos = (List<String>) relatorio.get("vetos");
        assertTrue(vetos.isEmpty());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultado = (Map<String, Object>) relatorio.get("resultado");
        assertEquals("VERDE (Baixo Risco)", resultado.get("classificacao"));
    }
    
    @Test
    @DisplayName("Deve gerar relatório correto para aplicação de baixo risco com vetos")
    public void testGerarRelatorioAplicacaoBaixoRiscoComVetos() {
        Map<String, Map<String, Double>> dados = criarDadosBaixoRisco();
        dados.get("experiencia").put("taxa_erro_usuario", 1.5);  // Adiciona um veto
        
        Map<String, Object> relatorio = calculadora.gerarRelatorio("Aplicacao Teste", "1.0", dados);
        
        assertEquals("BLOQUEADO", relatorio.get("status"));
        
        @SuppressWarnings("unchecked")
        List<String> vetos = (List<String>) relatorio.get("vetos");
        assertEquals(1, vetos.size());
    }
    
    @Test
    @DisplayName("Deve gerar relatório correto para aplicação de médio risco sem vetos")
    public void testGerarRelatorioAplicacaoMedioRiscoSemVetos() {
        Map<String, Map<String, Double>> dados = criarDadosMedioRisco();
        
        Map<String, Object> relatorio = calculadora.gerarRelatorio("Aplicacao Teste", "1.0", dados);
        
        assertEquals("CONDICIONADO", relatorio.get("status"));
        
        @SuppressWarnings("unchecked")
        List<String> vetos = (List<String>) relatorio.get("vetos");
        assertTrue(vetos.isEmpty());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultado = (Map<String, Object>) relatorio.get("resultado");
        assertEquals("AMARELO (Médio Risco)", resultado.get("classificacao"));
    }
    
    @Test
    @DisplayName("Deve gerar relatório correto para aplicação de alto risco sem vetos")
    public void testGerarRelatorioAplicacaoAltoRiscoSemVetos() {
        Map<String, Map<String, Double>> dados = criarDadosAltoRisco();
        
        Map<String, Object> relatorio = calculadora.gerarRelatorio("Aplicacao Teste", "1.0", dados);
        
        assertEquals("CONDICIONADO", relatorio.get("status"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultado = (Map<String, Object>) relatorio.get("resultado");
        assertEquals("VERMELHO (Alto Risco)", resultado.get("classificacao"));
    }
    
    @Test
    @DisplayName("Caso Qube: Deve avaliar corretamente a aplicação Qube AICUBE")
    public void testAvaliacaoQubeAICUBE() {
        Map<String, Map<String, Double>> dadosQube = new HashMap<>();
        
        // Dados de Bugs
        Map<String, Double> bugs = new HashMap<>();
        bugs.put("densidade_defeitos", 1.2);
        bugs.put("cobertura_testes", 94.0);
        bugs.put("bugs_criticos", 0.0);
        bugs.put("taxa_regressao", 0.5);
        bugs.put("mtbf", 980.0);
        dadosQube.put("bugs", bugs);
        
        // Dados de Performance
        Map<String, Double> performance = new HashMap<>();
        performance.put("tempo_resposta", 220.0);
        performance.put("percentil_95", 650.0);
        performance.put("utilizacao_recursos", 55.0);
        performance.put("escalabilidade", 8.0);
        performance.put("tempo_inicializacao", 3.8);
        performance.put("throughput", 150.0);
        dadosQube.put("performance", performance);
        
        // Dados de Segurança
        Map<String, Double> seguranca = new HashMap<>();
        seguranca.put("vulnerabilidades_criticas", 0.0);
        seguranca.put("vulnerabilidades_totais", 3.0);
        seguranca.put("owasp_top10", 100.0);
        seguranca.put("seguranca_dados", 100.0);
        seguranca.put("autenticacao_autorizacao", 2.0);
        seguranca.put("pentest", 2.0);
        dadosQube.put("seguranca", seguranca);
        
        // Dados de Experiência
        Map<String, Double> experiencia = new HashMap<>();
        experiencia.put("satisfacao_usuario", 75.0);
        experiencia.put("taxa_erro_usuario", 0.9);  // Ajustado para não acionar veto
        experiencia.put("tempo_conclusao_tarefa", 5.0);
        experiencia.put("acessibilidade", 96.0);
        experiencia.put("taxa_abandono", 3.2);
        experiencia.put("adocao_funcionalidades", 72.0);
        dadosQube.put("experiencia", experiencia);
        
        Map<String, Object> relatorio = calculadora.gerarRelatorio("Qube AICUBE", "1.0", dadosQube);
        ResultadoAvaliacao resultado = new ResultadoAvaliacao(relatorio);
        
        assertTrue(resultado.isAprovado());
        assertFalse(resultado.isBloqueado());
        assertTrue(resultado.isBaixoRisco());
        assertEquals(1.0, resultado.getPontuacaoTotal(), 0.01);
        assertTrue(resultado.getVetos().isEmpty());
        
        // Teste com taxa de erro alta (deve bloquear)
        experiencia.put("taxa_erro_usuario", 1.8);  // Aciona veto
        dadosQube.put("experiencia", experiencia);
        
        relatorio = calculadora.gerarRelatorio("Qube AICUBE", "1.0", dadosQube);
        resultado = new ResultadoAvaliacao(relatorio);
        
        assertTrue(resultado.isBloqueado());
        assertFalse(resultado.isAprovado());
        assertEquals(1, resultado.getVetos().size());
    }
    
    // Métodos auxiliares para criar dados de teste
    
    private Map<String, Map<String, Double>> criarDadosBaixoRisco() {
        Map<String, Map<String, Double>> dados = new HashMap<>();
        
        // Dados de Bugs - Baixo Risco
        Map<String, Double> bugs = new HashMap<>();
        bugs.put("densidade_defeitos", 1.0);
        bugs.put("cobertura_testes", 95.0);
        bugs.put("bugs_criticos", 0.0);
        bugs.put("taxa_regressao", 0.5);
        bugs.put("mtbf", 800.0);
        dados.put("bugs", bugs);
        
        // Dados de Performance - Baixo Risco
        Map<String, Double> performance = new HashMap<>();
        performance.put("tempo_resposta", 250.0);
        performance.put("percentil_95", 700.0);
        performance.put("utilizacao_recursos", 50.0);
        performance.put("escalabilidade", 8.0);
        performance.put("tempo_inicializacao", 4.0);
        performance.put("throughput", 120.0);
        dados.put("performance", performance);
        
        // Dados de Segurança - Baixo Risco
        Map<String, Double> seguranca = new HashMap<>();
        seguranca.put("vulnerabilidades_criticas", 0.0);
        seguranca.put("vulnerabilidades_totais", 3.0);
        seguranca.put("owasp_top10", 100.0);
        seguranca.put("seguranca_dados", 100.0);
        seguranca.put("autenticacao_autorizacao", 2.0);
        seguranca.put("pentest", 2.0);
        dados.put("seguranca", seguranca);
        
        // Dados de Experiência - Baixo Risco
        Map<String, Double> experiencia = new HashMap<>();
        experiencia.put("satisfacao_usuario", 75.0);
        experiencia.put("taxa_erro_usuario", 0.5);
        experiencia.put("tempo_conclusao_tarefa", 5.0);
        experiencia.put("acessibilidade", 96.0);
        experiencia.put("taxa_abandono", 3.0);
        experiencia.put("adocao_funcionalidades", 65.0);
        dados.put("experiencia", experiencia);
        
        return dados;
    }
    
    private Map<String, Map<String, Double>> criarDadosMedioRisco() {
        Map<String, Map<String, Double>> dados = criarDadosBaixoRisco();
        
        // Altera alguns dados para médio risco
        dados.get("bugs").put("densidade_defeitos", 3.0);
        dados.get("bugs").put("cobertura_testes", 75.0);
        
        dados.get("performance").put("tempo_resposta", 500.0);
        dados.get("performance").put("percentil_95", 1500.0);
        dados.get("performance").put("escalabilidade", 20.0);
        
        dados.get("seguranca").put("vulnerabilidades_totais", 10.0);
        dados.get("seguranca").put("owasp_top10", 95.0);
        
        dados.get("experiencia").put("satisfacao_usuario", 50.0);
        dados.get("experiencia").put("taxa_erro_usuario", 3.0);
        dados.get("experiencia").put("acessibilidade", 85.0);
        
        return dados;
    }
    
    private Map<String, Map<String, Double>> criarDadosAltoRisco() {
        Map<String, Map<String, Double>> dados = criarDadosMedioRisco();
        
        // Altera alguns dados para alto risco
        dados.get("bugs").put("densidade_defeitos", 6.0);
        dados.get("bugs").put("cobertura_testes", 65.0);
        
        dados.get("performance").put("tempo_resposta", 900.0);
        dados.get("performance").put("percentil_95", 2500.0);
        
        dados.get("seguranca").put("vulnerabilidades_totais", 20.0);
        dados.get("seguranca").put("owasp_top10", 85.0);
        dados.get("seguranca").put("seguranca_dados", 90.0);
        
        dados.get("experiencia").put("satisfacao_usuario", 35.0);
        dados.get("experiencia").put("tempo_conclusao_tarefa", 35.0);
        
        return dados;
    }
}