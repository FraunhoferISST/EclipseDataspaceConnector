from datetime import datetime
from elasticsearch import Elasticsearch

es = Elasticsearch("http://localhost:9200")

es.indices.refresh(index="auditlogging")

resp = es.search(index="auditlogging", query={"match_all": {}})
print("Got %d Hits:" % resp['hits']['total']['value'])
for hit in resp['hits']['hits']:
    message = hit["_source"]["event"]["original"]
    msgArray = message.split()
    print(len(msgArray))
