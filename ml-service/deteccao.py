import pandas as pd
import numpy as np
from sklearn.ensemble import IsolationForest

np.random.seed(42)

dados_normais = {
    'requisicoes_por_minuto': np.random.normal(100, 15, 95),
    'tempo_resposta_ms': np.random.normal(200, 30, 95),
    'erros_500': np.random.poisson(1, 95),
}
df_normal = pd.DataFrame(dados_normais)

dados_anomalos = pd.DataFrame({
    'requisicoes_por_minuto': [850, 900, 12, 780, 5, 950],
    'tempo_resposta_ms': [4500, 5200, 180, 3900, 150, 220],
    'erros_500': [45, 60, 0, 38, 0, 2],
})

df = pd.concat([df_normal, dados_anomalos], ignore_index=True)

modelo = IsolationForest(contamination='auto', random_state=42)
df['anomalia'] = modelo.fit_predict(df[['requisicoes_por_minuto', 'tempo_resposta_ms', 'erros_500']])

print(df[df['anomalia'] == -1])

def classificar_padrao(requisicoes, tempo_resposta, erros):
    if requisicoes > 500 and erros < 5:
        return "Possível Força Bruta (MITRE T1110)"
    elif tempo_resposta > 3000 and erros > 20:
        return "Possível Degradação de Serviço (OWASP API4:2023)"
    elif requisicoes < 20:
        return "Possível Indisponibilidade"
    else:
        return "Padrão não classificado"


anomalias = df[df['anomalia'] == -1]

for indice, linha in anomalias.iterrows():
    categoria = classificar_padrao(linha['requisicoes_por_minuto'], linha['tempo_resposta_ms'], linha['erros_500'])
    print(f"Linha {indice}: {categoria}")
