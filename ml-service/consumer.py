from kafka import KafkaConsumer
import json

consumer = KafkaConsumer(
    'requisicoes',
    bootstrap_servers='localhost:9092',
    auto_offset_reset='earliest',
    value_deserializer=lambda v: json.loads(v.decode('utf-8'))
)

contagem = {}
total = 0

print("Aguardando mensagens...")
try:
    for mensagem in consumer:
        evento = mensagem.value
        status = evento["status"]

        contagem[status] = contagem.get(status, 0) + 1
        total += 1

        if total % 10 == 0:
            print(total, contagem)
            contagem = {}
except KeyboardInterrupt:
    print("\nEncerrando consumidor.")
