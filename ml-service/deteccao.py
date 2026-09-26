"""
IAMS - Módulo de Detecção de Anomalias
Detecta padrões de tráfego via Isolation Forest, classifica por regra
(MITRE ATT&CK / OWASP) com fallback via LLM local (Ollama) para casos ambíguos.
"""
import pandas as pd
import numpy as np
from sklearn.ensemble import IsolationForest
import requests


def classificar_com_ollama(requisicoes, tempo_resposta, erros):
    prompt = f"""Você é um classificador de anomalias de tráfego de API.

    Dados observados:
    - Requisições por minuto: {requisicoes}
    - Tempo de resposta (ms): {tempo_resposta}
    - Erros 500: {erros}

    Categorias possíveis:
    - Possível Scraping/Enumeração de Dados (volume alto, sem erro, resposta normal)
    - Possível Exfiltração de Dados (tempo de resposta alto, sem muito erro)
    - Tráfego Legítimo Atípico (fora do padrão, mas sem indício claro de ataque)

    Responda em UMA linha, só com o nome da categoria mais provável, sem explicação."""

    try:
        resposta = requests.post(
            "http://localhost:11435/api/generate",
            json={"model": "phi3.5", "prompt": prompt, "stream": False, "options": {"temperature": 0}},
            timeout=30
        )
        return resposta.json()["response"].strip()
    except requests.exceptions.RequestException as e:
        return f"Erro ao consultar Ollama: {e}"


def classificar_padrao(requisicoes, tempo_resposta, erros):
    if requisicoes > 500 and erros < 5:
        return "Possível Força Bruta (MITRE T1110)"
    elif tempo_resposta > 3000 and erros > 20:
        return "Possível Degradação de Serviço (OWASP API4:2023)"
    elif tempo_resposta > 5000:
        return "Possível Degradação de Serviço - Latência Extrema (OWASP API4:2023)"
    elif requisicoes < 20:
        return "Possível Indisponibilidade"
    else:
        return classificar_com_ollama(requisicoes, tempo_resposta, erros)


if __name__ == "__main__":
    np.random.seed(42)

    dados_normais = {
        'requisicoes_por_minuto': np.random.normal(100, 15, 95),
        'tempo_resposta_ms': np.random.normal(200, 30, 95),
        'erros_500': np.random.poisson(1, 95),
    }
    df_normal = pd.DataFrame(dados_normais)

    dados_anomalos = pd.DataFrame({
        'requisicoes_por_minuto': [50, 900, 12, 780, 5, 950, 320],
        'tempo_resposta_ms': [14500, 5200, 180, 3900, 150, 220, 900],
        'erros_500': [0, 60, 0, 38, 0, 2, 9],
    })

    df = pd.concat([df_normal, dados_anomalos], ignore_index=True)

    modelo = IsolationForest(contamination=0.07, random_state=42)
    df['anomalia'] = modelo.fit_predict(df[['requisicoes_por_minuto', 'tempo_resposta_ms', 'erros_500']])

    print(df[df['anomalia'] == -1])

    anomalias = df[df['anomalia'] == -1]

    for indice, linha in anomalias.iterrows():
        categoria = classificar_padrao(linha['requisicoes_por_minuto'], linha['tempo_resposta_ms'], linha['erros_500'])
        print(f"Linha {indice}: {categoria}")