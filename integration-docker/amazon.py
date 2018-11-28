from flask import Flask

app = Flask(__name__)

@app.route("/public-hostname")
def metadata():
    return 'ec2-XX-XX-XX-XX.us-east-Y.compute.amazonaws.com'

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0", port=5200)
