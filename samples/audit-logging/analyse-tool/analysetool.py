from datetime import datetime
from elasticsearch import Elasticsearch
from dotenv import load_dotenv
import os
import json

load_dotenv()  # take environment variables from .env.

def list_registered_IDs(elasticServer,elasticIndex):
    listIDs = []

    elasticServer.indices.refresh(index=elasticIndex)
    
    resp = elasticServer.search(index=elasticIndex, query={"match_all": {}})
    print("Alle registrierten IDs, welche in dem S3 Bucket abgelegt worden sind:\n")
    print("--------------------------------------------------")
    print("EDC-ID ----- AWS-ID")
    for hit in resp['hits']['hits']:
        
        msg = msg = json.loads(hit["_source"]["message"])
        if msg["type"] == "AWSPut":
            print(msg["assetID"]+ " ---- " + msg["awsID"])
            entry = {"assetId" : msg["assetID"], "awsID": msg["awsID"]}
            listIDs.append(entry)
       
    
    return listIDs

def list_access_on_AWS_Object(elasticServer,elasticIndex,awsID):
    resp = elasticServer.search(index=elasticIndex, query={"match_all":{}})
    print("Got %d Hits:" % resp['hits']['total']['value'])
    for hit in resp['hits']['hits']:
        msg = hit["_source"]["message"]
    
        if awsID in msg and ("GET" in msg or "Get" in msg or "get" in msg):
            print("\n-----------------------------------------")
            print("ACCESS ON FILE: " + awsID+ "\n")
            print(msg)

def list_post_on_AWS_Object(elasticServer,elasticIndex,awsID):
    resp = elasticServer.search(index=elasticIndex, query={"match_all":{}})
    print("Got %d Hits:" % resp['hits']['total']['value'])
    for hit in resp['hits']['hits']:
        msg = hit["_source"]["message"]
        
        print(msg)
        if awsID in msg: # and ("POST" in msg or "Post" in msg or "post" in msg):
            print("\n-----------------------------------------")
            print("PUSH OF FILE: "+ awsID +"\n")
            print(msg)            




elasticsearchServer = os.getenv('elasticServer')
elasticsearchAWSIndex = os.getenv('elasticAWSIndex')
elasticsearchEDCIndex = os.getenv('elasticEDCIndex')

es = Elasticsearch("http://localhost:9200")

es.indices.refresh(index="edclogging")

idsDic = list_registered_IDs(es,"edclogging")

testEntry = {"assetId" : "test", "awsID": "test.txt"} #TODO delete
idsDic.append(testEntry) #TODO delete

for entry in idsDic:
    list_access_on_AWS_Object(es,"auditlogging",entry["awsID"])

for entry in idsDic:
    list_post_on_AWS_Object(es,"auditlogging",entry["awsID"])

          