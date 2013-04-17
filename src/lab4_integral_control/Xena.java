package lab4_integral_control;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Stopwatch;

public class Xena {

	final static double CIRCUMFERENCE = 56*Math.PI; //circumference in mm
	final static int RADIUS = 28; //in mm
	final static int SPEED = 240; //degrees per second
	final static double AXLELENGTH = 115; //L in mm
	final static double SCANPAUSE = 1847/3;

	static TouchSensor touch = new TouchSensor(SensorPort.S1);
	static UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S2);

	static double angle = 0; 
	static double x = 0;
	static double y = 0;

	//static int minDistance = 20;

	static final double GOALX = 180; //cm
	static final double GOALY = 180; //cm
	static final double STARTX = 100; //cm
	static final double STARTY = 0; //cm

	static boolean wasTooClose = false;
	static boolean wasTooFar = false;
	static boolean wasInRange = true;

	public static Stopwatch stopwatch = new Stopwatch();

	public static void move(int distance) { //distance in mm

		Motor.A.setSpeed(SPEED); 
		Motor.B.setSpeed(SPEED);
		double numRevolutions = distance/CIRCUMFERENCE; 
		double numDegrees = 360*numRevolutions; 

		Motor.A.rotate((int)(Math.ceil(numDegrees)), true);
		Motor.B.rotate((int)(Math.ceil(numDegrees)), false); //blocking until Motor B finishes rotating

		x = x + distance*Math.cos(angle*(Math.PI/180));
		y = y + distance*Math.sin(angle*(Math.PI/180));
	}

	public static void findObstacle() { 
		Motor.A.setSpeed(SPEED);
		Motor.B.setSpeed(SPEED);

		while((myGetDistance() > 20) && (myGetDistance() != 255)) {
			Motor.A.forward();
			Motor.B.forward();
		}

		Motor.A.setSpeed(0);
		Motor.B.setSpeed(0);

		pivot(-90);
		Motor.C.rotateTo(-90);
	}

	public static void followObstacle() {
		Motor.A.setSpeed(SPEED);
		Motor.B.setSpeed(SPEED);
		while (true) {
			System.out.println(myGetDistance());
			if (touch.isPressed()) {
				move(-100);
				pivot(-35);
			}
			if (myGetDistance() < 20) { //Too close to wall
				if(wasTooFar || wasInRange){ //Previously farther from wall
					wasTooClose = true;
					wasTooFar = false;
					wasInRange = false;

					pivot(-15);//pivot away from wall
				}
			}

			if (myGetDistance() > 24) { //Robot too far from wall TODO
				if (wasTooClose || wasInRange) {
					wasTooClose = false;
					wasTooFar = true;
					wasInRange = false;

					pivot(15); //pivot toward wall
				}
			}

			if ((myGetDistance() >= 20) && ( myGetDistance()<= 24)) { //In range
				if (wasTooClose) { //Was heading away from wall
					wasTooClose = false;
					wasTooFar = false;
					wasInRange = true;

					pivot(15); //pivot/straighten wheels toward wall
				}

				else if (wasTooFar) { //Was heading toward wall
					wasTooClose = false;
					wasTooFar = false;
					wasInRange = true;

					pivot(-15); //pivot/straighten wheels away from wall
				}
			}

			if (touch.isPressed()) {
				move(-100);
				pivot(-35);
			}

			move(50);
		}

	}


	public static void pivot(int degrees) {  //Negative 'degrees' pivots right
		Motor.A.setSpeed(SPEED);
		Motor.B.setSpeed(SPEED); //left wheel

		double rotateTime = (AXLELENGTH*degrees)/(RADIUS*(Motor.A.getSpeed() + Motor.B.getSpeed()));
		double numRotations = (Motor.A.getSpeed()*rotateTime);

		Motor.B.rotate((int)-numRotations,true);
		Motor.A.rotate((int)numRotations);

		angle = angle + degrees;

	}

	public static int myGetDistance() {
		int distance = sonic.getDistance();
		if (distance == 255) {
			distance = sonic.getDistance();
			if (distance == 255) {
				distance = sonic.getDistance();
			}
		}
		return distance;
	}

	public static void pause(int pauseTime) {
		try {
			Thread.sleep(pauseTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
