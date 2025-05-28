#!/usr/bin/env python3
"""
Calculadora de Risco de Qualidade - Banco Itaú
Governança de Tecnologia

Este script calcula o nível de risco de uma aplicação com base nos critérios
estabelecidos no Modelo de Risco de Qualidade do Banco Itaú.
"""

import csv
import json
from datetime import datetime

class CalculadoraRisco:
    def __init__(self):
        # Definição dos pesos das dimensões
        self.pesos_dimensoes = {
            "bugs": 0.25,
            "performance": 0.25,
            "seguranca": 0.30,
            "experiencia": 0.20
        }
        
        # Definição dos pesos dos critérios por dimensão
        self.pesos_criterios = {
            "bugs": {
                "densidade_defeitos": 0.25,
                "cobertura_testes": 0.20,
                "bugs_criticos": 0.30,
                "taxa_regressao": 0.15,
                "mtbf": 0.10
            },
            "performance": {
                "tempo_resposta": 0.25,
                "percentil_95": 0.25,
                "utilizacao_recursos": 0.15,
                "escalabilidade": 0.15,
                "tempo_inicializacao": 0.05,
                "throughput": 0.15
            },
            "seguranca": {
                "vulnerabilidades_criticas": 0.30,
                "vulnerabilidades_totais": 0.20,
                "owasp_top10": 0.20,
                "seguranca_dados": 0.15,
                "autenticacao_autorizacao": 0.10,
                "pentest": 0.05
            },
            "experiencia": {
                "satisfacao_usuario": 0.25,
                "taxa_erro_usuario": 0.20,
                "tempo_conclusao_tarefa": 0.15,
                "acessibilidade": 0.10,
                "taxa_abandono": 0.15,
                "adocao_funcionalidades": 0.15
            }
        }
        
        # Limiares para classificação de risco
        self.limiares = {
            "bugs": {
                "densidade_defeitos": {"baixo": 2, "medio": 5},
                "cobertura_testes": {"baixo": 90, "medio": 70, "invertido": True},
                "bugs_criticos": {"baixo": 0, "medio": 2},
                "taxa_regressao": {"baixo": 1, "medio": 3},
                "mtbf": {"baixo": 720, "medio": 168, "invertido": True}
            },
            "performance": {
                "tempo_resposta": {"baixo": 300, "medio": 800},
                "percentil_95": {"baixo": 800, "medio": 2000},
                "utilizacao_recursos": {"baixo": 60, "medio": 85},
                "escalabilidade": {"baixo": 10, "medio": 30},
                "tempo_inicializacao": {"baixo": 5, "medio": 15},
                "throughput": {"baixo": 100, "medio": 50, "invertido": True}
            },
            "seguranca": {
                "vulnerabilidades_criticas": {"baixo": 0, "medio": 1},
                "vulnerabilidades_totais": {"baixo": 5, "medio": 15},
                "owasp_top10": {"baixo": 100, "medio": 90, "invertido": True},
                "seguranca_dados": {"baixo": 100, "medio": 95, "invertido": True},
                "autenticacao_autorizacao": {"baixo": 2, "medio": 1},  # 2=Completo, 1=Parcial, 0=Mínimo
                "pentest": {"baixo": 2, "medio": 1}  # 2=Sem falhas críticas, 1=Médio risco, 0=Alto risco
            },
            "experiencia": {
                "satisfacao_usuario": {"baixo": 70, "medio": 40, "invertido": True},
                "taxa_erro_usuario": {"baixo": 2, "medio": 5},
                "tempo_conclusao_tarefa": {"baixo": 10, "medio": 30},
                "acessibilidade": {"baixo": 95, "medio": 80, "invertido": True},
                "taxa_abandono": {"baixo": 5, "medio": 15},
                "adocao_funcionalidades": {"baixo": 60, "medio": 30, "invertido": True}
            }
        }
        
    def classificar_criterio(self, dimensao, criterio, valor):
        """Classifica um critério como baixo (1), médio (2) ou alto (3) risco."""
        limiar = self.limiares[dimensao][criterio]
        invertido = limiar.get("invertido", False)
        
        if invertido:
            if valor >= limiar["baixo"]:
                return 1  # Baixo risco
            elif valor >= limiar["medio"]:
                return 2  # Médio risco
            else:
                return 3  # Alto risco
        else:
            if valor <= limiar["baixo"]:
                return 1  # Baixo risco
            elif valor <= limiar["medio"]:
                return 2  # Médio risco
            else:
                return 3  # Alto risco
    
    def calcular_pontuacao_dimensao(self, dimensao, valores):
        """Calcula a pontuação ponderada para uma dimensão."""
        pontuacao_total = 0
        peso_total = 0
        
        for criterio, valor in valores.items():
            if criterio in self.pesos_criterios[dimensao]:
                peso = self.pesos_criterios[dimensao][criterio]
                classificacao = self.classificar_criterio(dimensao, criterio, valor)
                pontuacao_total += classificacao * peso
                peso_total += peso
        
        # Garantir que temos pesos válidos
        if peso_total == 0:
            return 0
            
        return pontuacao_total / peso_total
    
    def calcular_risco_total(self, dados):
        """Calcula o risco total com base nas pontuações das dimensões."""
        pontuacoes_dimensoes = {}
        risco_total = 0
        
        for dimensao, criterios in dados.items():
            pontuacao = self.calcular_pontuacao_dimensao(dimensao, criterios)
            pontuacoes_dimensoes[dimensao] = pontuacao
            risco_total += pontuacao * self.pesos_dimensoes[dimensao]
        
        # Classificação final
        if risco_total <= 1.5:
            classificacao = "VERDE (Baixo Risco)"
        elif risco_total <= 2.2:
            classificacao = "AMARELO (Médio Risco)"
        else:
            classificacao = "VERMELHO (Alto Risco)"
        
        return {
            "pontuacao_total": risco_total,
            "classificacao": classificacao,
            "pontuacoes_dimensoes": pontuacoes_dimensoes
        }
    
    def verificar_vetos(self, dados):
        """Verifica se algum dos critérios de veto foi atingido."""
        vetos = []
        
        # Veto 1: Qualquer vulnerabilidade crítica de segurança não mitigada
        if dados.get("seguranca", {}).get("vulnerabilidades_criticas", 0) > 0:
            vetos.append("Vulnerabilidade crítica de segurança não mitigada")
        
        # Veto 2: Tempo de resposta P95 > 3000ms em operações críticas
        if dados.get("performance", {}).get("percentil_95", 0) > 3000:
            vetos.append("Tempo de resposta P95 > 3000ms em operações críticas")
        
        # Veto 3: Bugs que impeçam fluxo principal do cliente
        if dados.get("bugs", {}).get("bugs_criticos", 0) > 0:
            vetos.append("Bugs que impedem fluxo principal do cliente")
        
        # Veto 4: Taxa de erro em produção >1% para operações críticas
        if dados.get("experiencia", {}).get("taxa_erro_usuario", 0) > 1:
            vetos.append("Taxa de erro em produção >1% para operações críticas")
        
        # Veto 5: não podemos verificar automaticamente
        
        return vetos
    
    def gerar_relatorio(self, nome_aplicacao, versao, dados):
        """Gera um relatório detalhado com o resultado da avaliação."""
        resultado = self.calcular_risco_total(dados)
        vetos = self.verificar_vetos(dados)
        
        relatorio = {
            "nome_aplicacao": nome_aplicacao,
            "versao": versao,
            "data_avaliacao": datetime.now().strftime("%d/%m/%Y"),
            "resultado": resultado,
            "vetos": vetos,
            "status": "BLOQUEADO" if vetos else "APROVADO" if resultado["classificacao"].startswith("VERDE") else "CONDICIONADO",
            "dados_detalhados": dados
        }
        
        return relatorio
    
    def carregar_dados_csv(self, arquivo_csv):
        """Carrega dados de um arquivo CSV formatado corretamente."""
        dados = {
            "bugs": {},
            "performance": {},
            "seguranca": {},
            "experiencia": {}
        }
        
        mapeamento_dimensoes = {
            "Bugs": "bugs",
            "Performance": "performance",
            "Segurança": "seguranca",
            "Experiência": "experiencia"
        }
        
        mapeamento_criterios = {
            "Densidade de defeitos": "densidade_defeitos",
            "Cobertura de testes": "cobertura_testes",
            "Bugs críticos pendentes": "bugs_criticos",
            "Taxa de regressão": "taxa_regressao",
            "MTBF": "mtbf",
            "Tempo de resposta": "tempo_resposta",
            "Percentil 95 de tempo de resposta": "percentil_95",
            "Utilização de recursos": "utilizacao_recursos",
            "Escalabilidade": "escalabilidade",
            "Tempo de inicialização": "tempo_inicializacao",
            "Throughput": "throughput",
            "Vulnerabilidades críticas": "vulnerabilidades_criticas",
            "Vulnerabilidades totais": "vulnerabilidades_totais",
            "OWASP Top 10": "owasp_top10",
            "Segurança de dados": "seguranca_dados",
            "Autenticação/Autorização": "autenticacao_autorizacao",
            "Pentest": "pentest",
            "Satisfação do usuário": "satisfacao_usuario",
            "Taxa de erro do usuário": "taxa_erro_usuario",
            "Tempo de conclusão de tarefa": "tempo_conclusao_tarefa",
            "Acessibilidade": "acessibilidade",
            "Taxa de abandono": "taxa_abandono",
            "Adoção de funcionalidades": "adocao_funcionalidades"
        }
        
        try:
            with open(arquivo_csv, 'r', encoding='utf-8') as f:
                leitor = csv.DictReader(f)
                for linha in leitor:
                    if 'Dimensão' in linha and 'Critério' in linha and 'Valor Atual' in linha:
                        dimensao = mapeamento_dimensoes.get(linha['Dimensão'])
                        criterio = mapeamento_criterios.get(linha['Critério'])
                        valor = linha['Valor Atual']
                        
                        if dimensao and criterio and valor and valor.strip():
                            try:
                                dados[dimensao][criterio] = float(valor)
                            except ValueError:
                                print(f"Aviso: Valor inválido para {criterio}: {valor}")
            
            return dados
        except Exception as e:
            print(f"Erro ao carregar CSV: {e}")
            return None
    
    def salvar_relatorio(self, relatorio, arquivo_saida):
        """Salva o relatório em formato JSON."""
        try:
            with open(arquivo_saida, 'w', encoding='utf-8') as f:
                json.dump(relatorio, f, indent=2, ensure_ascii=False)
            print(f"Relatório salvo em {arquivo_saida}")
            return True
        except Exception as e:
            print(f"Erro ao salvar relatório: {e}")
            return False

if __name__ == "__main__":
    import sys
    
    if len(sys.argv) < 4:
        print("Uso: python calculadora_risco.py <nome_aplicacao> <versao> <arquivo_csv> [arquivo_saida]")
        sys.exit(1)
    
    nome_aplicacao = sys.argv[1]
    versao = sys.argv[2]
    arquivo_csv = sys.argv[3]
    arquivo_saida = sys.argv[4] if len(sys.argv) > 4 else f"relatorio_{nome_aplicacao.replace(' ', '_')}_{versao}.json"
    
    calc = CalculadoraRisco()
    dados = calc.carregar_dados_csv(arquivo_csv)
    
    if dados:
        relatorio = calc.gerar_relatorio(nome_aplicacao, versao, dados)
        
        # Imprimir resumo na tela
        print(f"\nRelatório de Risco - {nome_aplicacao} v{versao}")
        print("-" * 50)
        print(f"Classificação: {relatorio['resultado']['classificacao']}")
        print(f"Pontuação: {relatorio['resultado']['pontuacao_total']:.2f}")
        print(f"Status: {relatorio['status']}")
        
        if relatorio['vetos']:
            print("\nCritérios de veto atingidos:")
            for veto in relatorio['vetos']:
                print(f"- {veto}")
        
        print("\nPontuações por dimensão:")
        for dim, pontuacao in relatorio['resultado']['pontuacoes_dimensoes'].items():
            nome_dim = {"bugs": "Bugs", "performance": "Performance", 
                       "seguranca": "Segurança", "experiencia": "Experiência"}
            print(f"- {nome_dim[dim]}: {pontuacao:.2f}")
        
        # Salvar relatório completo
        calc.salvar_relatorio(relatorio, arquivo_saida)
    else:
        print("Não foi possível processar os dados de entrada.")
        sys.exit(1)