package lab4_integral_control;
import pq_as_heap_explicit.PQueue;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Stopwatch;

public class TestXena {

	final static double CIRCUMFERENCE = 56*Math.PI; //circumference in mm
	final static int RADIUS = 28; //in mm
	final static int SPEED = 240; //degrees per second
	final static double AXLELENGTH = 113; //L in mm
	//final static double AXLELENGTH = 100; //L in mm should be 109.4

	static TouchSensor touch = new TouchSensor(SensorPort.S1);
	static UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S2);

	static double angle = 90; 
	static double x = 0;
	static double y = 0;
	
	static double xhit;
	static double yhit;

	static final double GOALX = 80; //cm
	static final double GOALY = 180; //cm
	static final double STARTX = 0; //cm
	static final double STARTY = 0; //cm

	//TODO: remember to set x and y equal to start x and y when not 0

	//start
		//turn to facing goal (9/4?)
		//go until hit obstacle (or goal)
	
	//wall following:
		//rotate to the right
		//turn head to left
		//follow wall until hit m line or goal
	
	//hit m line:
		//turn head back to front
		//turn to goal
		//go to goal until hit obstacle or goal
	
	
	
	public static void move(int distance) { //distance in mm

		Motor.A.setSpeed(SPEED); 
		Motor.B.setSpeed(SPEED);
		double numRevolutions = distance/CIRCUMFERENCE; 
		double numDegrees = 360*numRevolutions; 

		Motor.A.rotate((int)(Math.ceil(numDegrees)), true);
		Motor.B.rotate((int)(Math.ceil(numDegrees)), false); //blocking until Motor B finishes rotating
		
		x = x + (distance*Math.cos(angle*(Math.PI/180)))/10.0; //to get x in cm
		y = y + (distance*Math.sin(angle*(Math.PI/180)))/10.0; //to get y in cm
		
		System.out.println("x: " + x);
		System.out.println("y: " + y);
		System.out.println("angle: " + angle);
	}

	public static void findObstacle() { 
		Motor.A.setSpeed(SPEED);
		Motor.B.setSpeed(SPEED);
		Stopwatch stopwatch = new Stopwatch();
		while((myGetDistance() > 20)) {
			Motor.A.forward();
			Motor.B.forward();
		}
		//Update x and y
		double time = stopwatch.elapsed()/1000.0; //= time in seconds
		double radius = RADIUS/10.0; //= radius in cm
		double speed = Motor.A.getSpeed();
		double Ur = speed*(Math.PI/180); //Ul = Ur
		double velocity = radius*Ur; //V = (Vr + Vl)/2; Vr = Vl
			
		x = x + velocity*Math.cos(angle*(Math.PI/180))*time;
		y = y + velocity*Math.sin(angle*(Math.PI/180))*time;
		
		//Update hit point:
		xhit = x;
		yhit = y;
		
		System.out.println("xhit: " + xhit);
		System.out.println("yhit: " + yhit);
		
//		System.out.println("radius: " + radius);
//		System.out.println("time: " + time);
//		System.out.println("speed: " + speed);
//		System.out.println("x: " + x);
//		System.out.println("y: " + y);
//		System.out.println("angle: " + angle);
		
		Motor.A.setSpeed(0);
		Motor.B.setSpeed(0);

		pivot(-90);
		Motor.C.rotateTo(-90);
	}

	public static void followObstacle() {
		Motor.A.setSpeed(SPEED);
		Motor.B.setSpeed(SPEED);
		int count = 0;
		while (!((y <= yhit + 20) && (x <= xhit + 5) && (x >= xhit - 5) && (count > 5))) {
			System.out.println(myGetDistance());
			if (touch.isPressed()) {
				move(-100);
				pivot(-35);
			}
			pause(50);
			if (myGetDistance() < 20) { //Way close to wall

					pivot(-15);//pivot away from wall
			}
			
			else if (myGetDistance() >= 16 && myGetDistance() < 20) { //A little too close to wall

					pivot(-8);//pivot away from wall
			}

			if (myGetDistance() >= 24) { //Way too far from wall TODO

					pivot(15); //pivot toward wall
			}
			
			else if (myGetDistance() > 24 && myGetDistance() < 28) { //A little too far from wall TODO

					pivot(8); //pivot toward wall
			}

			if (touch.isPressed()) {
				move(-100);
				pivot(-35);
			}
			
			
			move(50);
			
			count++;
		}

	}

	public static void pivot(int degrees) {  //Negative 'degrees' pivots right
		boolean isRightTurn = degrees < 0;
		degrees = Math.abs(degrees);
		
		Motor.A.setSpeed(SPEED);
		Motor.B.setSpeed(SPEED); //left wheel

		double rotateTime = (AXLELENGTH*degrees+0.0)/(RADIUS*(Motor.A.getSpeed() + Motor.B.getSpeed()));
		double desiredWheelDTheta = (Motor.A.getSpeed()*rotateTime); //in degrees

		int actualWheelDTheta = (int)desiredWheelDTheta;
		
		if(isRightTurn){
			Motor.A.rotate(-actualWheelDTheta,true);
			Motor.B.rotate(actualWheelDTheta,false);
		}
		else{
			Motor.B.rotate(-actualWheelDTheta,true);
			Motor.A.rotate(actualWheelDTheta,false);
		}
		pause(100);
		
//		System.out.println("desiredWheelDTheta: " + desiredWheelDTheta);
//		System.out.println("actualWheelDTheta: " + actualWheelDTheta);
		
		double wheelCircumference = (2*Math.PI)*(RADIUS/10.0);
		//System.out.println("wheelCircumference: " + wheelCircumference);//expected to be 17.6 (cm)
		
		double wheelTravelDistance = (actualWheelDTheta/360.0)*wheelCircumference;
		System.out.println("wheelTravelDistance: " + wheelTravelDistance);//expected to be 1.46 (for desired body rotation = 15 deg)
		
		double rotationRadius = (AXLELENGTH/10.0)/2.0; //expected to be 5.75
		//System.out.println("rotationRadius: " + rotationRadius);
		
		double rotationCircumference = (2*Math.PI)*rotationRadius;
		//System.out.println("rotationCircumference: " + rotationCircumference); //expected to be 36.12 deg
		
		
		//   bodyRotation(deg)  /  360(deg)   = wheelTravelDist / rotationCircumference
		
		double bodyDTheta = (360.0*wheelTravelDistance)/rotationCircumference;
		System.out.println("bodyDTheta: " + bodyDTheta);
		
		if(isRightTurn){
			angle = angle - bodyDTheta;
		}
		else{
			angle = angle + bodyDTheta;
		}
		
		System.out.println("angle: " + angle);


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

	
	public static int sideOfLineCheck(int x1, int y1, int x2, int y2, int testX, int testY){
		
		double result;
		result = ( ( y2-y1 ) / (x2-x1  ) );
		result=testY-y1 - result*(testX - x1);


		return (int)result;
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
