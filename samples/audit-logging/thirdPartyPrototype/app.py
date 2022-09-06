from flask import Flask
from markupsafe import escape
from flask import request
from flask import Response

app = Flask(__name__)

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
        file = f.read()
        data = {'file': file}
        return Response(data, mimetype='multipart/form-data')



@app.route('/file/<filename>', methods=['POST'])
def post_file(filename):
    data = request.files['file']

    data.save('testSave.txt')

    print(data)

    return "Got Input"