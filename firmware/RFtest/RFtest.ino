#include <Arduino.h>
#include <Wire.h>

void setup()
{
  Serial.begin(115200);
  helloWorld();
}
void loop()
{
  Serial.write("pang");
}
void helloWorld() {
  Serial.println("hello world");
}
