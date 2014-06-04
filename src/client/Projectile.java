package client;
import java.awt.Graphics;

/* Projectile Class */
/* This class provides a basis for all 
 * projectiles in the game */

public class Projectile {

	private int id;
	private double x, y;
	private double direction, speed;
	private long startTime;
	
	public Projectile(double x, double y, double dir, double speed)
	{
		this.x = x;
		this.y = y;
		this.direction = dir;
		this.speed = speed;
		this.startTime = System.currentTimeMillis();
	}
	
	public void update() {
		double xSpeed = Math.cos(direction)*speed;
		double ySpeed = Math.sin(direction)*speed;
		x += xSpeed;
		y += ySpeed;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}

	public double getDirection() {
		return direction;
	}

	public double getSpeed() {
		return speed;
	}
	
	public int getID() {
		return id;
	}

	public long getTime() {
		return System.currentTimeMillis()-startTime;
	}
	
	public void draw(Graphics g) {
		g.fillOval((int)x, (int)y, 4, 4);
	}
	
}
