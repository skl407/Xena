package lab4;
import pq_as_heap_explicit.PQueue;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Stopwatch;

public class Xena {

	final static double CIRCUMFERENCE = 56*Math.PI; //circumference in mm
	final static int RADIUS = 28; //in mm
	final static int SPEED = 240; //degrees per second
	final static double AXLELENGTH = 120; //L in mm
	//final static double AXLELENGTH = 110; //L in mm should be 109.4

	static TouchSensor touch = new TouchSensor(SensorPort.S1);
	static UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S2);

	static double angle = 90; 
	static double x = 0;
	static double y = 0;

	static double xhit;
	static double yhit;

	static final double GOALX = 80; //cm
	static final double GOALY = 180; //cm

	static final double M_LINE_ANGLE = 66.0375; //in degrees

	static int pivotCount;


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

		rotateToGoal(angle);
		Motor.C.rotateTo(0);

		Stopwatch stopwatch = new Stopwatch();


		while((myGetDistance() > 20) && (!inGoal())) { //Don't update x and y until out of loop
			//stopwatch.reset();
			//			Motor.A.forward();
			//			Motor.B.forward();
			move(50);
			//Update x and y
			//			double time = stopwatch.elapsed()/1000.0; //= time in seconds
			//			double radius = RADIUS/10.0; //= radius in cm
			//			double speed = Motor.A.getSpeed();
			//			double Ur = speed*(Math.PI/180); //Ul = Ur
			//			double velocity = radius*Ur; //V = (Vr + Vl)/2; Vr = Vl
			//			x = x + velocity*Math.cos(angle*(Math.PI/180))*time;
			//			y = y + velocity*Math.sin(angle*(Math.PI/180))*time;
		}




		//TODO
		if (myGetDistance() <= 20) {
			followObstacle();
		}

		moveTo(80,180);

		//Update hit point:
		xhit = x;
		yhit = y;

		//		System.out.println("xhit: " + xhit);
		//		System.out.println("yhit: " + yhit);

		//		System.out.println("radius: " + radius);
		//		System.out.println("time: " + time);
		//		System.out.println("speed: " + speed);
		//		System.out.println("x: " + x);
		//		System.out.println("y: " + y);
		//		System.out.println("angle: " + angle);

		Motor.A.setSpeed(0);
		Motor.B.setSpeed(0);

	}

	public static void followObstacle() {
		pivot(-90);
		Motor.C.rotateTo(-90);
		Motor.A.setSpeed(SPEED);
		Motor.B.setSpeed(SPEED);

		int count = 0;
		while (!(isInMLine(x,y) && (count > 5))) {
			System.out.println(myGetDistance());
			if (touch.isPressed()) {
				move(-100);
				pivot(-35);
			}
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
		
		System.out.println("x: " + x);
		System.out.println("y: " + y);
		System.out.println("angle: " + angle);

		if (!inGoal()){
			findObstacle();
		}

	}

	public static void pivot(double degrees) {  //Negative 'degrees' pivots right
		pivotCount++;
		if (pivotCount%3 == 0) {
			angle = angle + 1.35;
		}
		
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


	public static void pause(int pauseTime) {
		try {
			Thread.sleep(pauseTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void rotateToGoal(double curAngle) {
		pivot(M_LINE_ANGLE - angle);
	}

	public static boolean inGoal() {
		if ((x >= GOALX - 10) && (x <= GOALX + 10) && (y >= GOALY - 10) && (y <= GOALY + 10) ) {
			return true;
		}

		return false;
	}

	public static boolean pointAboveLine(double x1, double y1, double x2, double y2, double testX, double testY){

		double slope = ( ( y2-y1+0.0 ) / (x2-x1  ) );
		//System.out.println("slope: "+slope);
		double yIntercept = y1-((x1-0)*slope);
		double lineYAtTestX = yIntercept + slope*testX;

		return testY > lineYAtTestX;
	}
	private static boolean isInMLine(double x, double y){
		//bottom left
		double blx = 1;
		double bly= 8+9/4;
		//bottom right
		double brx = 0;
		double bry = -8;
		//top left
		double tlx = 81;
		double tly = 180+8+9/4;
		//top right
		double tRx = 80;
		double tRy = 180-8;

		//line1
		boolean isAboveRightLine=pointAboveLine(brx,bry,tRx,tRy,x,y);
		//line 2
		//sideOfLineCheck(tRx,tRy,tlx,tly,x,y);

		boolean isAboveLeftLine=pointAboveLine(tlx,tly,blx,bly,x,y);
		//System.out.println("("+x+","+y+") is above left line: " + isAboveLeftLine);
		//System.out.println("("+x+","+y+") is above right line: " + isAboveRightLine);
		boolean isOnMLine = !isAboveLeftLine && isAboveRightLine;
		return isOnMLine;
		//sideOfLineCheck(blx,bly,brx,bry,x,y);

	}

	public static void moveTo(double x1, double y1) {
		double dx = 10*(x1 - x);//mm
		double dy = 10*(y1 - y);//mm
		
		pivot((Math.atan(dy/dx)/(Math.PI/180) - angle));
		
		
		double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		move((int)dist);
	}

}
