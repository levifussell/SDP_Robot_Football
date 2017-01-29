#include "SerialCommand.h"
#include "SDPArduino.h"
#include <Wire.h>
#include <Arduino.h>

#define FRONT 5
#define RIGHT 3
#define BACK 4
#define LEFT 0
#define KICKERS 1

#define sensor A3

int run = 0;
int grabberUp = 1;

SerialCommand sCmd;

void loop(){
  
  float irReading = analogRead(sensor)*0.0048828125;  // value from sensor * (5/1024)
  //delay(50); // slow down serial port for printing
  //Serial.println(irReading);   // print the distance
  
  if (irReading > 2 && grabberUp) {
    grabberUp = 0;
    
    //grabber down
    motorControl(2,-100);
    delay(300);
  
    //stop grabber
    completeHalt();
    delay(600);
  }  
  
}

void test(){
  run = 1;
}

void dontMove(){
  motorStop(FRONT);
  motorStop(BACK);
  motorStop(LEFT);
  motorStop(RIGHT);
}

void spinmotor(){
  int motor = atoi(sCmd.next());
  int power = atoi(sCmd.next());
  motorForward(motor, power);
}

void motorControl(int motor, int power){
  if(power == 0){
    motorStop(motor);
  } else if(power > 0){
    motorBackward(motor, power);
  } else {
    motorForward(motor, -power);
  }
}

void rationalMotors(){
  int front = atoi(sCmd.next());
  int back  = atoi(sCmd.next());
  int left  = atoi(sCmd.next());
  int right = atoi(sCmd.next());
  motorControl(FRONT, front);
  motorControl(BACK, back);
  motorControl(LEFT, left);
  motorControl(RIGHT, right);
}

void pingMethod(){
  Serial.println("pang");
}

void kicker(){
  int type = atoi(sCmd.next());
  if(type == 0){
    motorStop(KICKERS);
  } else if (type == 1){
    motorForward(KICKERS, 100);
  } else {
    motorBackward(KICKERS, 100);
  }
}

void completeHalt(){
  motorAllStop();
}

void setup(){
  sCmd.addCommand("f", dontMove); 
  sCmd.addCommand("h", completeHalt); 
  sCmd.addCommand("motor", spinmotor); 
  sCmd.addCommand("r", rationalMotors); 
  sCmd.addCommand("ping", pingMethod); 
  sCmd.addCommand("kick", kicker); 
  SDPsetup();
  helloWorld();
  
  //set grabber
  //grabber up
  motorControl(2,100);
  delay(300);
  //stop grabber
  completeHalt();
}

