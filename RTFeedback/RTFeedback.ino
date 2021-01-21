/* Libraries */
#include <SoftwareSerial.h>
#include <Adafruit_NeoPixel.h>

/* Definitions */
#define RxD 2
#define TxD 3
#define FPIN 4
#define LPIN 11
#define LEDS 16

/* Globals */
SoftwareSerial BLE(RxD, TxD);
Adafruit_NeoPixel Leds(LEDS, FPIN);
uint32_t Red = Leds.Color(255, 0, 0);
uint32_t Green = Leds.Color(0, 255, 0);
uint32_t Blue = Leds.Color(0, 0, 255);

char BLEData[2];
int P = 0;

/**
 * Initialization.
*/
void setup() {
  BLE.begin(9600);
  Leds.begin();
  for (int i = FPIN; i <= LPIN; i++)
    pinMode(i, OUTPUT);
}

/**
 * BLE Parsing.
 */
void loop() {
  if (BLE.available()) {
    char c = BLE.read();
    if (c == ',') {
      P = 0;
      selectFeedback();
    } else {
      BLEData[P] = c;
      P++;
    }
  }
}

/**
 * Feedback Selection.
 */
void selectFeedback() {
  switch (BLEData[0]) {
    case '1':
      feedbackVT();
      break;
    case '2':
      feedbackVD();
      break;
    case '3':
      feedbackVP();
      break;
  }
}

/**
 * Vibro-Tactile Feedback.
*/
void feedbackVT() {
  for (int i = FPIN; i <= LPIN; i++)
    digitalWrite(i, LOW);

  switch (BLEData[1]) {
    case '0':
      digitalWrite(FPIN, HIGH);
      break;
    case '1':
      digitalWrite(FPIN + 1, HIGH);
      break;
    case '2':
      digitalWrite(FPIN + 2, HIGH);
      break;
    case '3':
      digitalWrite(FPIN + 3, HIGH);
      break;
    case '4':
      digitalWrite(FPIN + 4, HIGH);
      break;
    case '5':
      digitalWrite(FPIN + 5, HIGH);
      break;
    case '6':
      digitalWrite(FPIN + 6, HIGH);
      break;
    case '7':
      digitalWrite(FPIN + 7, HIGH);
      break;
  }
}

/**
 * Visual-Directional Feedback.
*/
void feedbackVD() {
  Leds.clear();

  switch (BLEData[1]) {
    case '0':
      Leds.fill(Green, 6, 4);
      break;
    case '1':
      Leds.fill(Green, 9, 4);
      break;
    case '2':
      Leds.fill(Green, 12, 4);
      break;
    case '3':
      Leds.fill(Red, 9, 4);
      break;
    case '4':
      Leds.fill(Red, 6, 4);
      break;
    case '5':
      Leds.fill(Red, 3, 4);
      break;
    case '6':
      Leds.fill(Green, 0, 4);
      break;
    case '7':
      Leds.fill(Green, 3, 4);
      break;
  }

  Leds.show();
}

/**
 * Visual-Positional Feedback.
*/
void feedbackVP() {
  Leds.clear();

  switch (BLEData[1]) {
    case '0':
      Leds.fill(Blue, 6, 4);
      break;
    case '1':
      Leds.fill(Blue, 9, 4);
      break;
    case '2':
      Leds.fill(Blue, 12, 4);
      break;
    case '3':
      Leds.setPixelColor(0, Blue);
      Leds.fill(Blue, 13, 3);
      break;
    case '4':
      Leds.fill(Blue, 0, 2);
      Leds.fill(Blue, 14, 2);
      break;
    case '5':
      Leds.fill(Blue, 0, 3);
      Leds.setPixelColor(15, Blue);
      break;
    case '6':
      Leds.fill(Blue, 0, 4);
      break;
    case '7':
      Leds.fill(Blue, 3, 4);
      break;
  }

  Leds.show();
}
