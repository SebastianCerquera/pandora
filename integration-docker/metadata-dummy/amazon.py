from flask import Flask

app = Flask(__name__)

COUNTER=0

@app.route("/public-hostname")
def metadata():
    global COUNTER
    COUNTER=COUNTER+1
    return 'ec2-XX-XX-XX-XX.us-east-' + str(COUNTER) + '.compute.amazonaws.com'

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0", port=5200)
