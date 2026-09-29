# IAMS — Intelligent Anomaly Monitoring System

Plataforma de monitoramento de segurança de APIs com detecção de anomalias via IA,
classificação por frameworks de segurança (MITRE ATT&CK / OWASP API Top 10) e
resposta adaptativa baseada em histórico.

## Arquitetura

```
Cliente → Backend (Spring Boot) → Kafka → ML Service (Python)
                                              ├─ Detecção de Anomalias (Isolation Forest)
                                              ├─ Classificador por regra (MITRE/OWASP)
                                              └─ Fallback via LLM local (Ollama)
```

## Status atual

**Implementado e testado:**
- Autenticação (registro, login, JWT) e endpoint protegido de teste
- CRUD de produtos (create, update parcial, get, delete)
- Log de requisições publicado no Kafka
- Detecção de anomalias com Isolation Forest (dados sintéticos)
- Classificação por regra + fallback via LLM local

**Planejado:**
- ML Service consumindo eventos reais do Kafka (hoje roda sobre dado sintético)
- MySQL como histórico de decisão (consulta antes de agir)
- Prometheus/Grafana com dashboards reais
- MongoDB para eventos brutos
- Injetor de falhas para demonstração sem tráfego real

## Stack

- **Backend:** Java 21, Spring Boot 4, Spring Security, Spring Kafka, MySQL
- **ML Service:** Python, scikit-learn, Ollama (LLM local)
- **Infraestrutura:** Kafka (modo KRaft), Docker Compose

## Como rodar localmente

**Pré-requisitos:** Java 21, Docker, MySQL, Python 3.13+

```bash
# Kafka
docker compose up -d

# Backend (variável de ambiente obrigatória)
export JWT_SECRET="sua-chave-secreta-aqui"
./mvnw spring-boot:run

# ML Service
cd ml-service
source .venv/bin/activate
python3 deteccao.py
```

## Endpoints principais

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/auth/register` | Cria usuário |
| POST | `/auth/login` | Autentica, retorna JWT |
| GET | `/auth/me` | Rota protegida de teste |
| POST | `/product/register` | Cria produto |
| PATCH | `/product/update` | Atualiza campos informados |
| GET | `/product/{productName}` | Busca produto |
| DELETE | `/product/{productName}` | Remove produto |