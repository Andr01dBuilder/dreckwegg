from flask import Flask
import json
import urllib
import requests
app = Flask(__name__)

def zweiterEintrag(paar):
	return paar[1]


link = "http://127.0.0.1/dreckwegg/georeport/v2/requests.json"

requestInhalt = "service_code=ZIEL&email=test@example.com&" 

headers={'content-type':'application/x-www-form-urlencoded'}

@app.route("/")
def hello():
	filehandle = urllib.urlopen("http://127.0.0.1/dreckwegg/georeport/v2/requests.json")
	requestdata = (filehandle.read())
	#werte = []
	liste = {}
	data1 = json.loads(requestdata)
	for data in data1:
		lat = data["lat"]
		lon = data["long"]
		lat = round(lat, 4)
		lon = round(lon, 4)
		position = "long="+str(lon)+"&"+"lat="+str(lat) #(str(lon)+","+str(lat))
		#werte.append(position)

		if position in liste:
			liste[position]=liste[position]+1
		else:
			liste[position]=1

		geordneteListe = sorted( [(pos, anzahl) for pos, anzahl in liste.items()], key = zweiterEintrag, reverse = True)
	#print json.dumps(werte)
	requestInhalt2 = requestInhalt+geordneteListe[0][0]
	response = requests.post(link, data=requestInhalt2, headers=headers)
	print(requestInhalt2)
	print(response.text)
	return(str(response.status_code))


if __name__ == "__main__":
    app.run()

