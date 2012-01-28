#include <SPI.h>
#include <Adb.h>
#include <Servo.h>

#define  LED1    3
#define  LED2    4
#define  SERVO1  5
#define  SERVO2  6

Servo servos[2];

Connection * connection;

// Elapsed time for ADC sampling
long lastTime;

// Event handler for shell connection; called whenever data sent from Android to Microcontroller
void adbEventHandler(Connection * connection, adb_eventType event, uint16_t length, uint8_t * data)
{
  // Data packets contain two bytes, first byte is pin number, second byte is state
  // For servos, in the range of [0..180], for LEDs, 0 or 1
  if (event == ADB_CONNECTION_RECEIVE)
  {
    int pin = data[0];
    switch (pin) {
    case 0x3:
      digitalWrite(LED1, data[1]);
      break;
    case 0x4:
      digitalWrite(LED2, data[1]);
      break;
    case 0x5:
      servos[0].write(data[1]);
      break;
    case 0x6:
      servos[1].write(data[1]);
      break;
    default:
      break;
    }
  }
}

void setup()
{

  // Init serial port for debugging
  Serial.begin(57600);

  // Attach servos
  servos[0].attach(SERVO1);
  servos[1].attach(SERVO2);

  // Init LEDs
  pinMode(LED1, OUTPUT);
  pinMode(LED2, OUTPUT);

  // Initially on
  digitalWrite(LED1, 1);
  digitalWrite(LED2, 1);

  // Init the ADB subsystem.  
  ADB::init();

  // Open an ADB stream to the phone's shell. Auto-reconnect. Use port number 4568
  connection = ADB::addConnection("tcp:4567", true, adbEventHandler);  
}

void loop()
{
  // Poll the ADB subsystem.
  ADB::poll();
}



