package br.com.itau.governanca.risco;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Testes unitários para a classe ResultadoAvaliacao
 */
public class ResultadoAvaliacaoTest {
    
    @Test
    @DisplayName("Deve criar corretamente um ResultadoAvaliacao a partir de um relatório")
    public void testCriarResultadoAvaliacao() {
        Map<String, Object> relatorio = criarRelatorioTeste("VERDE (Baixo Risco)", 1.2, "APROVADO", false);
        
        ResultadoAvaliacao resultado = new ResultadoAvaliacao(relatorio);
        
        assertEquals("Aplicacao Teste", resultado.getNomeAplicacao());
        assertEquals("1.0", resultado.getVersao());
        assertEquals(1.2, resultado.getPontuacaoTotal());
        assertEquals("VERDE (Baixo Risco)", resultado.getClassificacao());
        assertEquals("APROVADO", resultado.getStatus());
        assertTrue(resultado.getVetos().isEmpty());
    }
    
    @Test
    @DisplayName("Deve identificar corretamente status aprovado")
    public void testIsAprovado() {
        Map<String, Object> relatorio = criarRelatorioTeste("VERDE (Baixo Risco)", 1.2, "APROVADO", false);
        ResultadoAvaliacao resultado = new ResultadoAvaliacao(relatorio);
        
        assertTrue(resultado.isAprovado());
        assertFalse(resultado.isBloqueado());
        assertFalse(resultado.isCondicionado());
    }
    
    @Test
    @DisplayName("Deve identificar corretamente status bloqueado")
    public void testIsBloqueado() {
        Map<String, Object> relatorio = criarRelatorioTeste("VERDE (Baixo Risco)", 1.2, "BLOQUEADO", true);
        ResultadoAvaliacao resultado = new ResultadoAvaliacao(relatorio);
        
        assertTrue(resultado.isBloqueado());
        assertFalse(resultado.isAprovado());
        assertFalse(resultado.isCondicionado());
    }
    
    @Test
    @DisplayName("Deve identificar corretamente status condicionado")
    public void testIsCondicionado() {
        Map<String, Object> relatorio = criarRelatorioTeste("AMARELO (Médio Risco)", 1.8, "CONDICIONADO", false);
        ResultadoAvaliacao resultado = new ResultadoAvaliacao(relatorio);
        
        assertTrue(resultado.isCondicionado());
        assertFalse(resultado.isAprovado());
        assertFalse(resultado.isBloqueado());
    }
    
    @Test
    @DisplayName("Deve identificar corretamente classificação de baixo risco")
    public void testIsBaixoRisco() {
        Map<String, Object> relatorio = criarRelatorioTeste("VERDE (Baixo Risco)", 1.2, "APROVADO", false);
        ResultadoAvaliacao resultado = new ResultadoAvaliacao(relatorio);
        
        assertTrue(resultado.isBaixoRisco());
        assertFalse(resultado.isMedioRisco());
        assertFalse(resultado.isAltoRisco());
    }
    
    @Test
    @DisplayName("Deve identificar corretamente classificação de médio risco")
    public void testIsMedioRisco() {
        Map<String, Object> relatorio = criarRelatorioTeste("AMARELO (Médio Risco)", 1.8, "CONDICIONADO", false);
        ResultadoAvaliacao resultado = new ResultadoAvaliacao(relatorio);
        
        assertTrue(resultado.isMedioRisco());
        assertFalse(resultado.isBaixoRisco());
        assertFalse(resultado.isAltoRisco());
    }
    
    @Test
    @DisplayName("Deve identificar corretamente classificação de alto risco")
    public void testIsAltoRisco() {
        Map<String, Object> relatorio = criarRelatorioTeste("VERMELHO (Alto Risco)", 2.5, "CONDICIONADO", false);
        ResultadoAvaliacao resultado = new ResultadoAvaliacao(relatorio);
        
        assertTrue(resultado.isAltoRisco());
        assertFalse(resultado.isBaixoRisco());
        assertFalse(resultado.isMedioRisco());
    }
    
    @Test
    @DisplayName("Deve retornar corretamente as pontuações por dimensão")
    public void testGetPontuacoesDimensoes() {
        Map<String, Object> relatorio = criarRelatorioTeste("VERDE (Baixo Risco)", 1.2, "APROVADO", false);
        ResultadoAvaliacao resultado = new ResultadoAvaliacao(relatorio);
        
        Map<String, Double> pontuacoes = resultado.getPontuacoesDimensoes();
        assertEquals(1.0, pontuacoes.get("bugs"));
        assertEquals(1.2, pontuacoes.get("performance"));
        assertEquals(1.1, pontuacoes.get("seguranca"));
        assertEquals(1.3, pontuacoes.get("experiencia"));
    }
    
    // Método auxiliar para criar um relatório de teste
    private Map<String, Object> criarRelatorioTeste(String classificacao, double pontuacao, String status, boolean comVetos) {
        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("nome_aplicacao", "Aplicacao Teste");
        relatorio.put("versao", "1.0");
        relatorio.put("status", status);
        
        // Cria o resultado com a classificação e pontuação
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("classificacao", classificacao);
        resultado.put("pontuacao_total", pontuacao);
        
        // Adiciona pontuações por dimensão
        Map<String, Double> pontuacoesDimensoes = new HashMap<>();
        pontuacoesDimensoes.put("bugs", 1.0);
        pontuacoesDimensoes.put("performance", 1.2);
        pontuacoesDimensoes.put("seguranca", 1.1);
        pontuacoesDimensoes.put("experiencia", 1.3);
        resultado.put("pontuacoes_dimensoes", pontuacoesDimensoes);
        
        relatorio.put("resultado", resultado);
        
        // Adiciona vetos se necessário
        List<String> vetos = new ArrayList<>();
        if (comVetos) {
            vetos.add("Vulnerabilidade crítica de segurança não mitigada");
        }
        relatorio.put("vetos", vetos);
        
        // Adiciona dados detalhados vazios
        relatorio.put("dados_detalhados", new HashMap<String, Map<String, Double>>());
        
        return relatorio;
    }
}