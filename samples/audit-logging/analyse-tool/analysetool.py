from datetime import datetime
from elasticsearch import Elasticsearch
from dotenv import load_dotenv
import os

load_dotenv()  # take environment variables from .env.

elasticsearchServer = os.getenv('elasticServer')
elasticsearchIndex = os.getenv("elasticIndex")

es = Elasticsearch(elasticsearchServer)

es.indices.refresh(index=elasticsearchIndex)

resp = es.search(index=elasticIndex, query={"match_all": {}})
print("Got %d Hits:" % resp['hits']['total']['value'])
for hit in resp['hits']['hits']:
    message = hit["_source"]["event"]["original"]
    msgArray = message.split()
    print(len(msgArray))
