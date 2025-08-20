from flask import Flask, request, jsonify
app = Flask(__name__)

@app.route("/kakaotalk-messages", methods=["POST"])
def kakaotalk():
    return jsonify({"result":"OK"}), 200

@app.route("/sms", methods=["POST"])
def sms():
    return jsonify({"result":"OK"}), 200

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8081)
