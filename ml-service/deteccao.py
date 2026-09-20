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
    'requisicoes_por_minuto': [850, 900, 12, 780, 5],
    'tempo_resposta_ms': [4500, 5200, 180, 3900, 150],
    'erros_500': [45, 60, 0, 38, 0],
})

df = pd.concat([df_normal, dados_anomalos], ignore_index=True)

modelo = IsolationForest(contamination=0.05, random_state=42)
df['anomalia'] = modelo.fit_predict(df[['requisicoes_por_minuto', 'tempo_resposta_ms', 'erros_500']])

print(df[df['anomalia'] == -1])
