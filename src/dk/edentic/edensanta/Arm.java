package dk.edentic.edensanta;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class Arm {
	static int armShown;
	static RegulatedMotor arm;
	static RegulatedMotor hand;
	static RegulatedMotor eyes;
	static int rotate = 110;
	static int shakeHand = 0;
	static int handAngle = 150;
	
	public Arm() {
		armShown = 0;
		arm = new EV3LargeRegulatedMotor(MotorPort.A);
		hand = new EV3LargeRegulatedMotor(MotorPort.B);
		eyes = new EV3MediumRegulatedMotor(MotorPort.C);
		hand.setSpeed(1000);
		eyes.setSpeed(1000);
	}
	
	public void openArm() {
		if(armShown == 0) {
			LCD.drawString("Open arm", 0, 0);
			arm.rotate(rotate);
			armShown = 1;
		}
	}
	
	public void closeArm() {
		if(armShown == 1) {
			LCD.drawString("Close arm", 0, 0);
			arm.rotate(-rotate);
			armShown = 0;
		}
	}
	
	public void shakeHand(int times) {
		for(int i = 0; i < times; i++) {
			hand.rotate(handAngle);
			hand.rotate(-handAngle);
		}
	}
	
	public void rotateEyes() {
		eyes.rotate(2000);
	}
}
