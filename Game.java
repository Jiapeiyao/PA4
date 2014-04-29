package Game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Game {
	public static final int DEFAULT_CRITTERS = 4;
	public static final int IMAGE_SIZE = 600;
	public static final int OBJECT_SIZE = 60;

	
	private final Image image;
	private final Critter critterFactory;
	private List<Critter> critterList;
	private boolean isOver;
	Graphics graphics;
	private int CRITTERS;

	
	Game() {
		image= new BufferedImage( IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
		graphics = image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0,0,IMAGE_SIZE, IMAGE_SIZE);
		graphics.setColor(Color.BLACK);
		critterFactory = new SquareCritter();
		CRITTERS = DEFAULT_CRITTERS;
	}
	
	public void setNumOfCritter(int n){ this.CRITTERS = n; }
	
	public boolean isGameOver(){ return isOver; }
	
	public void start(){
		isOver = false;
		critterList = new ArrayList();
		for (int i=0; i<CRITTERS; i++){
			critterList.add(critterFactory.makeCritter());
		}
	}
	
	public void draw(){
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0,0,IMAGE_SIZE, IMAGE_SIZE);
		for (Critter critter: critterList){
			critter.move();
			critter.draw(graphics);
		}
	}
	
	public void processClick(int X, int Y){
		for (Critter critter: critterList){
			if (critter.within(X, Y)){
				critterList.remove(critter);
				if (critterList.isEmpty()){ isOver = true; }
				return;
			}
		}
	}
	
	
	
	Image getImage(){ return this.image; }
	
	abstract private class Critter{
		int x;
		int y;
		int deltaX;
		int deltaY;
		Color c;
		
		Critter(){
			int maxDelta = (int)Math.pow(IMAGE_SIZE, 1/2);
			int minDelta = (int)Math.pow(IMAGE_SIZE, 1/4);
			int sign = ((int)(Math.random()*2)==1)?1:-1;
			x = (int)(Math.random()*IMAGE_SIZE);
			y= (int)(Math.random()*IMAGE_SIZE);
			deltaX = sign*((int)(Math.random()*(maxDelta-minDelta)+minDelta));
			sign = ((int)(Math.random()*2)==1)?1:-1;
			deltaY = sign*((int)(Math.random()*(maxDelta-minDelta)+minDelta));
			c = setRandomColor();
		}
		
		Critter makeCritter(){
			int randomNum = (int)(Math.random()*6);
			switch (randomNum){
			case 0:
				return new SquareCritter();
			case 1:
				return new RoundCritter();
			case 2:
				return new WowCritter();
			case 3:
				return new ArcCritter();
			case 4:
				return new PolyCritter();
			case 5:
				return new RingCritter();
			}
			return null;
		}
		
		abstract void draw( Graphics graphics );
		abstract boolean within(int x, int y);
		
		void move(){
			x=(x+deltaX+IMAGE_SIZE)%IMAGE_SIZE;
			y=(y+deltaY+IMAGE_SIZE)%IMAGE_SIZE;
		}
		
		Color setRandomColor(){
			float r = (float)Math.random();
			float g = (float)Math.random();
			float b = (float)Math.random();
			return new Color(r, g, b);
		}
	}
		
		private class SquareCritter extends Critter{
			SquareCritter(){ super(); }
			void draw(Graphics graphics) {
				graphics.setColor(c);
				graphics.fillRect(x-OBJECT_SIZE/2, y-OBJECT_SIZE/2, OBJECT_SIZE, OBJECT_SIZE);
			}
			boolean within(int X, int Y){
				return (X>x-OBJECT_SIZE/2)&&(X<x+OBJECT_SIZE/2)&&(Y>y-OBJECT_SIZE/2)&&(Y<y+OBJECT_SIZE/2);
			}
		}
		
		private class RoundCritter extends Critter{
			RoundCritter(){ super(); }
			void draw(Graphics graphics) {
				graphics.setColor(c);
				graphics.fillOval(x-OBJECT_SIZE/2, y-OBJECT_SIZE/2, OBJECT_SIZE, OBJECT_SIZE);
				
			}
			boolean within(int X, int Y){
				return (X-x)*(X-x)+(Y-y)*(Y-y)<OBJECT_SIZE*OBJECT_SIZE/4;
			}
		}
		
		private class WowCritter extends RoundCritter{
			WowCritter(){ super(); }
			void draw(Graphics graphics) {
				super.draw(graphics);
				c = setRandomColor();
			}
		}

		private class ArcCritter extends RoundCritter{
			private static final int START_ANGLE = 45;
			private static final int ANGLE = 270;
			ArcCritter(){ super(); }
			void draw(Graphics graphics){
				graphics.setColor(c);
				graphics.fillArc(x-OBJECT_SIZE/2, y-OBJECT_SIZE/2, OBJECT_SIZE, OBJECT_SIZE, START_ANGLE, ANGLE);
			}
			boolean within(int X, int Y){
				return super.within(X, Y)&&isInDegreeRange(getAngle(X,Y));
			}
			//helper functions of within(X,Y)
			int getAngle(int X, int Y){
				double dY = y - Y;
				double dX = X - x;
				int degree = 0;
				try {
					degree = (int)(Math.acos(dX/Math.sqrt(dX*dX+dY*dY))*180/Math.PI);
					if (dY < 0){ degree = 360-degree; }//2rd and 3th Quadrant
					return degree;
				}
				catch (Exception e){
					return 999;
				}
			}
			boolean isInDegreeRange(int degree){
				int END_ANGLE = 45+270;
				return (degree>=START_ANGLE)&&(degree<=END_ANGLE)||(degree==999);
			}
		}
		
		private class PolyCritter extends Critter{
			int[] xPoints;
			int[] yPoints1;
			int[] yPoints2;
			PolyCritter(){ super(); }
			void draw(Graphics graphics){
				graphics.setColor(c);
				xPoints = new int[]{x, 
						(int)(x-OBJECT_SIZE*Math.sqrt(3)/4), 
						(int)(x+OBJECT_SIZE*Math.sqrt(3)/4)};
				yPoints1 = new int[]{y+OBJECT_SIZE/2, y-OBJECT_SIZE/4, y-OBJECT_SIZE/4,};
				yPoints2 = new int[]{y-OBJECT_SIZE/2, y+OBJECT_SIZE/4, y+OBJECT_SIZE/4};
				graphics.fillPolygon(xPoints, yPoints1, 3);
				graphics.fillPolygon(xPoints, yPoints2, 3);
			}
			boolean within(int X, int Y){
				return (isInTriangle(X, Y, xPoints, yPoints1)||isInTriangle(X, Y, xPoints, yPoints2));
			}
			//helper functions of within(X,Y) using Heron's formula
			private double distance(int X1, int Y1, int X2, int Y2){ 
				return Math.sqrt((X1-X2)*(X1-X2)+(Y1-Y2)*(Y1-Y2)); 
			}
			private double AreaOfTriangle(int X1, int Y1, int X2, int Y2, int X3, int Y3){
				double a = distance(X1,Y1,X2,Y2);
				double b = distance(X2,Y2,X3,Y3);
				double c = distance(X1,Y1,X3,Y3);
				double p = (a+b+c)/2;
				return Math.sqrt(p*(p-a)*(p-b)*(p-c));
			}
			private boolean isInTriangle(int X, int Y, int[] xCor, int[] yCor){
				double A = AreaOfTriangle(xCor[0],yCor[0], xCor[1],yCor[1], xCor[2],yCor[2]);
				double A1 = AreaOfTriangle(X,Y, xCor[0],yCor[0], xCor[1],yCor[1]);
				double A2 = AreaOfTriangle(X,Y, xCor[0],yCor[0], xCor[2],yCor[2]);
				double A3 = AreaOfTriangle(X,Y, xCor[1],yCor[1], xCor[2],yCor[2]);
				int difference = (int)Math.abs(A - A1 - A2 - A3);
				return difference<3;
			}
			
		}
		
		private class RingCritter extends WowCritter{
			RingCritter(){ super(); }
			void draw(Graphics graphics) {
				super.draw(graphics);
				graphics.setColor(Color.WHITE);
				graphics.fillOval(x-OBJECT_SIZE/4, y-OBJECT_SIZE/4, OBJECT_SIZE/2, OBJECT_SIZE/2);
			}
			boolean within(int X, int Y){
				return super.within(X,Y) && ((X-x)*(X-x)+(Y-y)*(Y-y)>OBJECT_SIZE*OBJECT_SIZE/16);
			}
		}
}
