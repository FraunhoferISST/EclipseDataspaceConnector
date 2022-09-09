from flask import Flask
from markupsafe import escape
from flask import request
from flask import Response
import requests

app = Flask(__name__)

print("START SERVER")

@app.route("/<filename>")
def hello_world(filename):
    path = "files/" + escape(filename) + ".txt"
    with open(path) as f:
        lines = f.readlines()

    return lines

@app.route('/file/<filename>', methods=['GET'])
def get_file(filename):
    path = "files/" + escape(filename) + ".txt"
    with open(path) as f:
        value = f.readlines()
        return value


@app.route('/file/<filename>', methods=['POST'])
def post_file(filename):
    data = request.data

    path = "files/" + escape(filename) + ".txt"

    print(data)

    return "Got Input"

@app.get('/logging/create')
def create_log():
    # some JSON:
    x = '{"sourceID" : "Meinasdb", "dataID" : "file.txt", "timestamp": "Jetszt", "logText" : "adsasd"}'

    print(request.remote_addr)

    headers = {'Content-Type': 'application/json'}
    r = requests.post('http://'+ request.remote_addr + ':8183/logging/auditlogging/newLogs', data=x,headers=headers)

    print(r)

    return "Worked"
