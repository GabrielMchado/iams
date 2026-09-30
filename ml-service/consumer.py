from kafka import KafkaConsumer
import json

consumer = KafkaConsumer(
    'requisicoes',
    bootstrap_servers='localhost:9092',
    auto_offset_reset='earliest',
    value_deserializer=lambda v: json.loads(v.decode('utf-8'))
)

print("Aguardando mensagens...")
try:
    for mensagem in consumer:
        print(mensagem.value)
except KeyboardInterrupt:
    print("\nEncerrando consumidor.")
