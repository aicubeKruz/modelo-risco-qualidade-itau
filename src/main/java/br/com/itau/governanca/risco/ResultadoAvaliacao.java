package br.com.itau.governanca.risco;

import java.util.Map;
import java.util.List;

/**
 * Classe que representa o resultado de uma avaliação de risco
 */
public class ResultadoAvaliacao {
    private final String nomeAplicacao;
    private final String versao;
    private final double pontuacaoTotal;
    private final String classificacao;
    private final Map<String, Double> pontuacoesDimensoes;
    private final List<String> vetos;
    private final String status;
    
    /**
     * Construtor que cria um ResultadoAvaliacao a partir do relatório gerado pela CalculadoraRisco
     * @param relatorio O relatório gerado pela CalculadoraRisco
     */
    @SuppressWarnings("unchecked")
    public ResultadoAvaliacao(Map<String, Object> relatorio) {
        this.nomeAplicacao = (String) relatorio.get("nome_aplicacao");
        this.versao = (String) relatorio.get("versao");
        
        Map<String, Object> resultado = (Map<String, Object>) relatorio.get("resultado");
        this.pontuacaoTotal = (double) resultado.get("pontuacao_total");
        this.classificacao = (String) resultado.get("classificacao");
        this.pontuacoesDimensoes = (Map<String, Double>) resultado.get("pontuacoes_dimensoes");
        
        this.vetos = (List<String>) relatorio.get("vetos");
        this.status = (String) relatorio.get("status");
    }
    
    /**
     * Verifica se a aplicação foi aprovada
     * @return true se foi aprovada, false caso contrário
     */
    public boolean isAprovado() {
        return "APROVADO".equals(status);
    }
    
    /**
     * Verifica se a aplicação foi bloqueada
     * @return true se foi bloqueada, false caso contrário
     */
    public boolean isBloqueado() {
        return "BLOQUEADO".equals(status);
    }
    
    /**
     * Verifica se a aplicação foi aprovada com condições
     * @return true se foi aprovada com condições, false caso contrário
     */
    public boolean isCondicionado() {
        return "CONDICIONADO".equals(status);
    }
    
    /**
     * Verifica se a aplicação tem baixo risco
     * @return true se tem baixo risco, false caso contrário
     */
    public boolean isBaixoRisco() {
        return classificacao.startsWith("VERDE");
    }
    
    /**
     * Verifica se a aplicação tem médio risco
     * @return true se tem médio risco, false caso contrário
     */
    public boolean isMedioRisco() {
        return classificacao.startsWith("AMARELO");
    }
    
    /**
     * Verifica se a aplicação tem alto risco
     * @return true se tem alto risco, false caso contrário
     */
    public boolean isAltoRisco() {
        return classificacao.startsWith("VERMELHO");
    }
    
    /**
     * Retorna o nome da aplicação
     * @return o nome da aplicação
     */
    public String getNomeAplicacao() {
        return nomeAplicacao;
    }
    
    /**
     * Retorna a versão da aplicação
     * @return a versão da aplicação
     */
    public String getVersao() {
        return versao;
    }
    
    /**
     * Retorna a pontuação total de risco
     * @return a pontuação total
     */
    public double getPontuacaoTotal() {
        return pontuacaoTotal;
    }
    
    /**
     * Retorna a classificação de risco
     * @return a classificação de risco
     */
    public String getClassificacao() {
        return classificacao;
    }
    
    /**
     * Retorna as pontuações por dimensão
     * @return as pontuações por dimensão
     */
    public Map<String, Double> getPontuacoesDimensoes() {
        return pontuacoesDimensoes;
    }
    
    /**
     * Retorna os vetos aplicados
     * @return os vetos aplicados
     */
    public List<String> getVetos() {
        return vetos;
    }
    
    /**
     * Retorna o status da avaliação
     * @return o status da avaliação
     */
    public String getStatus() {
        return status;
    }
}