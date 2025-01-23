
- [Supermarket demo application](#supermarket-demo-application)
  - [Building project](#building-project)
  - [Running with docker for dev testing](#running-with-docker-for-dev-testing)

# Supermarket demo application

## Building project

```sh
mvn install

mvn -f inventory/pom.xml clean package exec:exec@rmi exec:exec@build
mvn -f ordermanager/pom.xml clean package exec:exec@rmi exec:exec@build
mvn -f markethub/pom.xml clean package exec:exec@rmi exec:exec@build

mvn -f orderprocessing/pom.xml clean package exec:exec@rmi exec:exec@build
```

## Running with docker for dev testing

```sh
docker network create --driver=bridge --subnet=172.19.0.0/16 --gateway=172.19.0.1 mainnet 

docker stop activemq-artemis
docker run --rm -d --net mainnet \
    -e ANONYMOUS_LOGIN=true \
    --name activemq-artemis -p 61616:61616 -p 8161:8161 apache/activemq-artemis:2.39.0

docker stop inventory
docker run -d --rm --net mainnet \
    -p 7070:8080 \
    -e OTEL_JAVAAGENT_ENABLED="false" \
    --name inventory inventory:1.0.0

docker stop ordermanager
docker run -d --rm --net mainnet \
    -p 7071:8080 \
    -e OTEL_JAVAAGENT_ENABLED="false" \
    -e SPRING_ARTEMIS_BROKER_URL="tcp://activemq-artemis:61616" \
    --name ordermanager ordermanager:1.0.0

docker stop markethub
docker run -d --rm --net mainnet \
    -p 7080:8080 \
    -e OTEL_JAVAAGENT_ENABLED="false" \
    -e INVENTORY_URL="http://inventory:8080" \
    -e ORDERMANAGER_URL="http://ordermanager:8080" \
    --name markethub markethub:1.0.0

# curl on inventory
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "Bitcoin", "quantity": 50000, "unitMarketPrice": 35000}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "Ethereum", "quantity": 10000, "unitMarketPrice": 2000}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "Apple", "quantity": 20000, "unitMarketPrice": 180}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "Tesla", "quantity": 15000, "unitMarketPrice": 250}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "Amazon", "quantity": 8000, "unitMarketPrice": 3200}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "Google", "quantity": 12000, "unitMarketPrice": 2700}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "Microsoft", "quantity": 25000, "unitMarketPrice": 330}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "Meta", "quantity": 30000, "unitMarketPrice": 150}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "NVIDIA", "quantity": 10000, "unitMarketPrice": 450}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "Dogecoin", "quantity": 1000000, "unitMarketPrice": 0.07}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "Cardano", "quantity": 500000, "unitMarketPrice": 0.35}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "Solana", "quantity": 50000, "unitMarketPrice": 20}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "Netflix", "quantity": 18000, "unitMarketPrice": 650}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "AMD", "quantity": 40000, "unitMarketPrice": 120}'
curl -X POST http://localhost:7070/createItem -H "Content-Type: application/json" -d '{ "itemName": "BerkshireHathaway", "quantity": 3000, "unitMarketPrice": 500000}'


docker stop markethub ordermanager inventory


```


test

curl -X POST http://localhost:8080/createItem -H "Content-Type: application/json" -d '{ "itemName": "Bitcoin", "quantity": 50000, "unitMarketPrice": 35000}'

curl -X 'POST' \
  'http://localhost:8080/updateMarketPrice' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": 0,
  "itemName": "Bitcoin",
  "quantity": 0,
  "unitMarketPrice": 1500
}'