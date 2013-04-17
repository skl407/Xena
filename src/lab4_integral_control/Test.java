package lab4_integral_control;

import java.util.Random;

import lejos.nxt.Button;

public class Test{

	public static void main(String[] args) {

//		int y;
//		int x; 
//		
//		x = 5;
//		y = 11;
//		
//		System.out.println(isInMLine(x,y));
		
Button.waitForAnyPress();
testRotationInPlace(true);

	}
	private static boolean isInMLine(int x, int y){
		//bottom left
		int blx = 0;
		int bly= -233;
		//bottom right
		int brx = 360;
		int bry = -609;
		//top left
		int tlx = 0;
		int tly = -217;
		//top right
		int tRx = 360;
		int tRy = -593;
		
		//line1
		int line1result=TestXena.sideOfLineCheck(brx,bry,tRx,tRy,x,y);
		//line 2
		//sideOfLineCheck(tRx,tRy,tlx,tly,x,y);
		
		int line3result=TestXena.sideOfLineCheck(tlx,tly,blx,bly,x,y);
		
		boolean isOnMLine = (line3result < 0) && (line1result > 0);
		return isOnMLine;
		//sideOfLineCheck(blx,bly,brx,bry,x,y);


		
		
				
	}
	
	private static void testRotationInPlace(boolean toTheLeft){
		int sign = 1;
		if(!toTheLeft){
			sign = -1;
		}
		Random rand = new Random();
		
		
		int desired_angle = sign*90;
		while(true){
			
			
			
			while (sign*(desired_angle - TestXena.angle) > 0) {
				//if less than 15 degrees left, just rotate to finish
				double diff = 1+Math.abs(desired_angle-TestXena.angle);
				if(diff < 15){
					TestXena.pivot(sign*(int)diff);
				}
				//randomize between rotation patterns
				else{
					//rotate 15
					if(rand.nextBoolean()){
						TestXena.pivot(sign*15);
					}
					else{
						if(rand.nextBoolean()){
							//rotate 15 then 8
							TestXena.pivot(sign*15);
							TestXena.pivot(sign*8);
						}
						else{
						//rotate 8
							TestXena.pivot(sign*8);
						}
					}
				}
			}
			
			Button.waitForAnyPress();
			desired_angle *= 2;
			
		}
	}
}
