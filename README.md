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
- Tratamento global de erros com respostas JSON padronizadas
- Validação de entrada no cadastro de produto
- Consumidor Kafka básico em Python (ainda sem ligação com a detecção)

**Planejado:**
- ML Service consumindo eventos reais do Kafka (hoje roda sobre dado sintético)
- MySQL como histórico de decisão (consulta antes de agir)
- Prometheus/Grafana com dashboards reais
- MongoDB para eventos brutos
- Injetor de falhas para demonstração sem tráfego real
- Tempo de resposta no evento do Kafka e agregação por janela de tempo no consumidor Python

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
python3 -m venv .venv            # só na primeira vez
source .venv/bin/activate
pip install -r requirements.txt  # só na primeira vez
python3 deteccao.py
```

## Endpoints principais

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/auth/register` | Cria usuário |
| POST | `/auth/login` | Autentica, retorna JWT |
| GET | `/auth/me` | Rota protegida de teste |
| POST | `/product/register` | Cria produto |
| PATCH | `/product/{productName}` | Atualiza campos informados |
| PUT | `/product/{productName}` | Substitui produto por completo |
| GET | `/product/{productName}` | Busca produto |
| DELETE | `/product/{productName}` | Remove produto |

## Limitações conhecidas

- O fallback via Ollama (modelo local, 3.8B parâmetros) apresenta viés de posição
  em casos de sinal fraco: a ordem das categorias no prompt pode mudar a resposta
  quando o dado não aponta claramente pra nenhuma categoria. Em dados com sinal
  mais forte, a resposta se manteve estável mesmo reordenando o prompt. Reforça
  a decisão de nunca confiar cegamente na saída do LLM — por isso a arquitetura
  sempre prevê revisão humana e histórico de decisão.
- Rota protegida sem token devolve 403 em vez de 401 (ajuste planejado, via `AuthenticationEntryPoint`).
- O 409 em `/auth/register` revela que o email já existe. É um trade-off aceito: os 409 repetidos chegam ao Kafka e servem de sinal de enumeração pro detector.
- O motivo da rejeição do JWT (expirado, assinatura inválida, malformado) aparece só no log do servidor, ainda não no evento do Kafka.

## Tratamento de erros

As falhas devolvem JSON no formato `{"message": "..."}`, tratadas num `@RestControllerAdvice` central. Detalhes internos (stack trace, mensagens do framework) ficam só no log do servidor.

| Status | Quando |
|--------|--------|
| 400 | Corpo malformado, validação de campo falhou ou PATCH sem nenhum valor alterado |
| 401 | Credenciais inválidas no login |
| 404 | Produto ou rota inexistente |
| 405 | Método HTTP não suportado pela rota |
| 409 | Email ou produto já cadastrado |
| 500 | Erro inesperado (mensagem genérica) |

As respostas de erro também geram evento no tópico Kafka `requisicoes`, que é o sinal que o detector vai usar.