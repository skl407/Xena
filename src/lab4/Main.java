package lab4;

import lejos.nxt.Button;
import lejos.nxt.Sound;

public class Main {

	public static void main(String[] args) {
		Button.waitForAnyPress();
		Xena.findObstacle();
		Button.waitForAnyPress();
	}
}
