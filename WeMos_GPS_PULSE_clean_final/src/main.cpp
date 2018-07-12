#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include "TinyGPS++.h"
#include "SoftwareSerial.h"




#define SERIAL_BUFFER_SIZE 256
#define PROCESSING_VISUALIZER 1
#define SERIAL_PLOTTER  2
#define FIREBASE_HOST "https://iot-android-ivana-angel.firebaseio.com/"
#define FIREBASE_AUTH "wVPUSuNbPzNiq7wdOXO99covLuOavbYLHAYqxs15"
#define clientID "iangeell"


const char* ssid = "ANGELiph";
const char* password = "i123angeell5";
const char* mqtt_server = "m23.cloudmqtt.com";
const char* mqtt_username = "ycstaqvf";
const char* mqtt_password = "mcv2ILaYwu0f";
const char* pulseTestTopic = "/tests/pulse";

WiFiClient espClient;
PubSubClient client(espClient);

//GPS
TinyGPSPlus gps;
bool waitingData = true;
double latitude;
double longitude;
double speedKm;
static const int RXPin = 4, TXPin = 5;
static const uint32_t GPSBaud = 9600;
SoftwareSerial ss(RXPin, TXPin, false, 256);



//  Variables
int pulsePin = 0;                 // firul mov conectat la pinul analog 0
int blinkPin = D4;
int fadePin = 14;
int fadeRate = 0;

bool stop = false;

// variabile volatile folosite in isr
volatile int BPM;
volatile int Signal;
volatile int IBI = 600;             // intervalul de timp dintre bataile inimii
volatile boolean Pulse = false;
volatile boolean QS = false;        // true daca s-a depistat o bataie a inimii

volatile int rate[10];                    // va contine ultimele 10 valori IBI
volatile unsigned long sampleCounter = 0;          // numara timpii dintre batai
volatile unsigned long lastBeatTime = 0;           // used to find IBI
volatile int P =512;                      // punct de maxim
volatile int T = 512;                     // punct de minim
volatile int thresh = 530;                // pragul unde se va detecta pulsul
volatile int amp = 0;                   // aplitudinea pulsului
volatile boolean firstBeat = true;
volatile boolean secondBeat = false;

static boolean serialVisual = true;


//variabile pentru timing si leduri
unsigned long lastTime;
unsigned long thisTime;
unsigned long fadeTime;


///////////////METODE DE CALCUL PENTRU PULS/////////////////////

void ledFadeToBeat(){
    fadeRate -= 15;                         //  se seteaza valoarea de fade
    fadeRate = constrain(fadeRate,0,255);   //  keep LED fade value from going into negative numbers!
    analogWrite(fadePin,fadeRate);          //  fade LED
  }

void getPulse(){
//  cli();                                      // opreste intreruperile
  Signal = analogRead(pulsePin);              // citeste datele de la senzor
  sampleCounter += 2;                         // tine evidenta timpului in mS
  int N = sampleCounter - lastBeatTime;       // monitorizeaza ultima data cand a avut loc o bataie pentru a evita zgomotele

    //  cauta punctul de maxim si minim dintr-un wave
  if(Signal < thresh && N > (IBI/5)*3){       // se evita zgomotele dicrotice (cand este detectata o dubla bataie) prin asteptarea 3/5 din ultimul IBI inregistrat
    if (Signal < T){                        // T e pct de minim
      T = Signal;                         // Daca valoarea citita este mai mica decat minimul curent, atunci minimul va fi valoarea respectiva
    }
  }

  if(Signal > thresh && Signal > P){          // ajuta la evitarea zgomotelor
    P = Signal;                             // Daca valoarea este mai mare decat minimul si mai mare si decat maximul,
  }                                        // noul maxim va fi valoarea citita

  //  se cauta bataia inimii
  // signal surges up in value every time there is a pulse
  if (N > 500){    //era 250                               // evita zgomote cu frecvente inalte
    if ( (Signal > thresh) && (Pulse == false) && (N > (IBI/5)*3) ){
      Pulse = true;                               // s-a depistat un puls
    //  digitalWrite(blinkPin,HIGH);                // turn on pin 13 LED
      IBI = sampleCounter - lastBeatTime;         // se masoara timpul dintre batai in mS
      lastBeatTime = sampleCounter;               // tine evidenta timpului

      if(secondBeat){
        secondBeat = false;
        for(int i=0; i<=9; i++){
          rate[i] = IBI;
        }
      }

      if(firstBeat){
        firstBeat = false;
        secondBeat = true;
//        sei();                               // porneste iar intreruperile
        return;
      }


      // runningTotal va tine totalul ultimelor 10 valori IBI
      word runningTotal = 0;          //word e echivalentul usigned int

      for(int i=0; i<=8; i++){
        rate[i] = rate[i+1];
        runningTotal += rate[i];
      }

      rate[9] = IBI;                          // se adauga ultima valoare ibi in vectorul rate
      runningTotal += rate[9];                // se aduna ultima valoare ibi la runningtotal
      runningTotal /= 10;                     // facem media ultimelor 10 valori ibi
      BPM = 60000/runningTotal;               // calculam cate batai sunt intr-un minut
      QS = true;                              // avem o bataie

    }
  }

  if (Signal < thresh && Pulse == true){   // cand valorile coboara, bataia s-a finalizat
    //digitalWrite(blinkPin,LOW);            // turn off pin 13 LED
    Pulse = false;
    amp = P - T;                           // calculam amplitudinea
    thresh = amp/2 + T;                    // setam pragul la 50% din amplitudine
    P = thresh;
    T = thresh;
  }

  if (N > 2500){                           // daca au trecut 2.5 secunde fara sa fie depistata vreo bataie datele sunt resetate
    thresh = 530;
    P = 512;
    T = 512;
    lastBeatTime = sampleCounter;
    firstBeat = true;
    secondBeat = false;
  }

//  sei();                                   // se repornesc intreruperile
}// sfarsit isr





/////////////////////////WiFi + MQTT///////////////////////////

void wifi_setup() {
  delay(10);
  // Serial.println();
  // Serial.println();
  // Serial.print("Connecting to ");
  // Serial.println(ssid);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
  //  Serial.print(".");
  }
  // Serial.println("");
  // Serial.println("WiFi connected");
  // Serial.println("IP address: ");
  // Serial.println(WiFi.localIP());
}



void callback(char* topic, byte* payload, unsigned int length) {
  String topicString = topic;

  // Serial.println("Callback update.");
  // Serial.println("Topic: ");
  // Serial.println(SERIAL_BUFFER_SIZE);
  // Serial.print(topicString);

  if(payload[0] == '1') {

    client.publish("/tests/confirm", String(BPM).c_str());
    client.publish("/tests/speed", String(speedKm).c_str());
    client.publish("/tests/coordinates/latitude", String(latitude).c_str());
    client.publish("/tests/coordinates/longitude", String(longitude).c_str());

  } else if (payload[0] == '2') {
    client.publish("/tests/fieldCoordinates/latitude", String(latitude).c_str());
    client.publish("/tests/fieldCoordinates/longitude", String(longitude).c_str());
  }

}


void reconnect() {
  while (!client.connected()) {
//  Serial.print("Attempting MQTT connection...");
  // Incercam reconectarea
  if (client.connect(clientID, mqtt_username, mqtt_password)) {
//    Serial.println("connected");
    if(client.subscribe(pulseTestTopic)) {
//      Serial.println("Subscribed to ");
//      Serial.print(pulseTestTopic);
    }

  } else {
    // Serial.print("failed, rc=");
    // Serial.print(client.state());
    // Serial.println(" try again in 5 seconds");
    // Asteptam 5 secunde inainte de a reincerca
    delay(5000);
  }
}
}


void retriveDataFromGPS() {
 Serial.print(F("Location & Speed: "));
  if (gps.location.isValid())
  {
    latitude = gps.location.lat();
    longitude = gps.location.lng();
    speedKm = gps.speed.kmph();
    Serial.print(gps.location.lat(), 6);
    Serial.print(F(","));
    Serial.print(gps.location.lng(), 6);
    Serial.println();
    Serial.print(gps.speed.mph());
  }
  else
  {
   Serial.print(F("INVALID"));
  }


 Serial.println();

}

void GPSloop() {
  while (ss.available() > 0) {

    if (gps.encode(ss.read())) {
      //Serial.print("gps.read: OK");
      // Serial.println();
      // Serial.println(ss.read());
      retriveDataFromGPS();
    }
  }
}




void setup() {
  // digitalWrite(LED_BUILTIN, LOW);
  // pinMode(blinkPin,OUTPUT);         // setam pinul principal care sa bipaie cand se inregistreaza o bataie a inimii
  // pinMode(fadePin,OUTPUT);          // pin ul care ar trebui sa aiba un efect de fade la inregistrarea pulsului
  Serial.begin(115200);             // setam rata baud la 115200 pentru a vedea in Serial monitor
  ss.begin(GPSBaud);
//  ss.begin(GPSBaud);
//  Serial.print(SERIAL_BUFFER_SIZE);
  lastTime = micros(); //initializam variabila lastTime cu nr de microsecunde care au trecut de cand placuta a inceput sa ruleze programul

  wifi_setup();
  client.setServer(mqtt_server, 13090);
  client.setCallback(callback);

}

void loop() {

  if(!client.connected()) {
    reconnect();
  }
  client.loop();


  thisTime = micros();
  if(thisTime - lastTime > 2000){ // verificam daca au trecut 2mS
    lastTime = thisTime;
    getPulse();
    GPSloop();

  }

  if (QS == true){     // s-a detectat o bataie
                       // BPM si IBI au fost determinate
        fadeRate = 255;         // fade effect
        fadeTime = millis();    // Set the fade timer to fade the LED



        QS = false;
  }

}
