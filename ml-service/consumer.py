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
    soma_tempo = 0
    com_tempo = 0
    for mensagem in consumer:
        evento = mensagem.value
        status = evento["status"]

        contagem[status] = contagem.get(status, 0) + 1
        total += 1

        tempo = evento.get("tempoRespostaMs")
        if tempo is not None:
            soma_tempo += tempo
            com_tempo += 1

        if total % 10 == 0:
            media = soma_tempo / com_tempo if com_tempo > 0 else None
            print(total, contagem, "média ms:", media)
            contagem = {}
            soma_tempo = 0
            com_tempo = 0

except KeyboardInterrupt:
    print("\nEncerrando consumidor.")
    if total % 10 != 0:
        print("janela parcial:", contagem)